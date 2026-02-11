import React, { useRef, useState } from 'react';
import { Upload, Camera, FileImage, X, CheckCircle, AlertCircle, Loader } from 'lucide-react';
import ocrService from '../services/ocrService';
import { OcrResponse } from '../types/ocr';
import Button from './Button';
import Card from './Card';

interface ImageUploadOcrProps {
  onOcrComplete: (result: OcrResponse) => void;
  onError?: (error: string) => void;
}

const ImageUploadOcr: React.FC<ImageUploadOcrProps> = ({ onOcrComplete, onError }) => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [progress, setProgress] = useState(0);
  const [result, setResult] = useState<OcrResponse | null>(null);
  const [engine, setEngine] = useState<'gemini' | 'tesseract'>('gemini');
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    // Validar tamanho (10MB)
    if (file.size > 10 * 1024 * 1024) {
      onError?.('Arquivo muito grande. Tamanho máximo: 10MB');
      return;
    }

    // Validar tipo
    if (!file.type.startsWith('image/')) {
      onError?.('Por favor, selecione uma imagem válida');
      return;
    }

    setSelectedFile(file);
    setResult(null);

    // Criar preview
    const reader = new FileReader();
    reader.onload = (e) => {
      setPreviewUrl(e.target?.result as string);
    };
    reader.readAsDataURL(file);
  };

  const handleProcessImage = async () => {
    if (!selectedFile) return;

    setIsProcessing(true);
    setProgress(0);

    // Simular progresso
    const progressInterval = setInterval(() => {
      setProgress((prev) => Math.min(prev + 10, 90));
    }, 200);

    try {
      const response = await ocrService.processarImagem(selectedFile, engine);

      clearInterval(progressInterval);
      setProgress(100);
      setResult(response);

      if (response.sucesso) {
        onOcrComplete(response);
      } else {
        onError?.(response.mensagem || 'Erro ao processar imagem');
      }
    } catch (error: any) {
      clearInterval(progressInterval);
      const errorMsg = error.response?.data?.mensagem || 'Erro ao processar imagem';
      onError?.(errorMsg);
      console.error('Erro no OCR:', error);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleClear = () => {
    setSelectedFile(null);
    setPreviewUrl(null);
    setResult(null);
    setProgress(0);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleButtonClick = () => {
    fileInputRef.current?.click();
  };

  return (
    <Card className="p-6">
      <div className="space-y-4">
        {/* Header */}
        <div className="flex items-center gap-3">
          <div className="p-2 bg-blue-100 rounded-lg">
            <Camera className="w-6 h-6 text-blue-600" />
          </div>
          <div>
            <h3 className="font-semibold text-gray-900">Catalogação Automática com OCR</h3>
            <p className="text-sm text-gray-600">
              Tire uma foto da capa do livro para preencher automaticamente os dados
            </p>
          </div>
        </div>

        {/* Upload Area */}
        {!previewUrl && (
          <div
            onClick={handleButtonClick}
            className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-blue-500 hover:bg-blue-50 transition-colors cursor-pointer"
          >
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              onChange={handleFileSelect}
              className="hidden"
            />
            <Upload className="w-12 h-12 text-gray-400 mx-auto mb-3" />
            <p className="text-gray-700 font-medium mb-1">
              Clique para selecionar imagem
            </p>
            <p className="text-sm text-gray-500">
              JPG, PNG ou TIFF até 10MB
            </p>
          </div>
        )}

        {/* Engine Selection (Only show if not processing) */}
        {!isProcessing && !result && (
          <div className="flex gap-4 justify-center py-2">
            <label className={`flex items-center gap-2 px-3 py-2 rounded-lg border cursor-pointer transition-colors ${engine === 'gemini' ? 'bg-blue-50 border-blue-500 text-blue-700' : 'border-gray-200 hover:bg-gray-50'}`}>
              <input
                type="radio"
                name="engine"
                value="gemini"
                checked={engine === 'gemini'}
                onChange={() => setEngine('gemini')}
                className="text-blue-600 focus:ring-blue-500"
              />
              <span className="font-medium">Gemini AI (Recomendado)</span>
            </label>
            <label className={`flex items-center gap-2 px-3 py-2 rounded-lg border cursor-pointer transition-colors ${engine === 'tesseract' ? 'bg-gray-100 border-gray-400 text-gray-800' : 'border-gray-200 hover:bg-gray-50'}`}>
              <input
                type="radio"
                name="engine"
                value="tesseract"
                checked={engine === 'tesseract'}
                onChange={() => setEngine('tesseract')}
                className="text-gray-600 focus:ring-gray-500"
              />
              <span className="font-medium">Tesseract (Local)</span>
            </label>
          </div>
        )}

        {/* Preview & Processing */}
        {previewUrl && (
          <div className="space-y-4">
            {/* Image Preview */}
            <div className="relative">
              <img
                src={previewUrl}
                alt="Preview"
                className="w-full max-h-64 object-contain rounded-lg border border-gray-200"
              />
              {!isProcessing && !result && (
                <button
                  onClick={handleClear}
                  className="absolute top-2 right-2 p-1 bg-red-500 text-white rounded-full hover:bg-red-600 transition-colors"
                >
                  <X size={16} />
                </button>
              )}
            </div>

            {/* Progress Bar */}
            {isProcessing && (
              <div className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-600 flex items-center gap-2">
                    <Loader className="w-4 h-4 animate-spin" />
                    Processando imagem...
                  </span>
                  <span className="text-blue-600 font-medium">{progress}%</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                    style={{ width: `${progress}%` }}
                  />
                </div>
              </div>
            )}

            {/* Result */}
            {result && (
              <div className="bg-gray-50 rounded-lg p-4 space-y-3">
                <div className="flex items-center gap-2">
                  {result.sucesso ? (
                    <>
                      <CheckCircle className="w-5 h-5 text-green-600" />
                      <span className="font-medium text-green-900">
                        OCR Concluído! Confiança: {result.confianca}%
                      </span>
                    </>
                  ) : (
                    <>
                      <AlertCircle className="w-5 h-5 text-amber-600" />
                      <span className="font-medium text-amber-900">
                        {result.mensagem}
                      </span>
                    </>
                  )}
                </div>

                {result.sucesso && (
                  <div className="text-sm space-y-1">
                    {result.titulo && (
                      <p>
                        <span className="text-gray-600">Título:</span>{' '}
                        <span className="font-medium text-gray-900">{result.titulo}</span>
                      </p>
                    )}
                    {result.autor && (
                      <p>
                        <span className="text-gray-600">Autor:</span>{' '}
                        <span className="font-medium text-gray-900">{result.autor}</span>
                      </p>
                    )}
                    {result.isbn && (
                      <p>
                        <span className="text-gray-600">ISBN:</span>{' '}
                        <span className="font-medium text-gray-900">{result.isbn}</span>
                      </p>
                    )}
                    {result.ano && (
                      <p>
                        <span className="text-gray-600">Ano:</span>{' '}
                        <span className="font-medium text-gray-900">{result.ano}</span>
                      </p>
                    )}
                  </div>
                )}
              </div>
            )}

            {/* Actions */}
            <div className="flex gap-2">
              {!isProcessing && !result && (
                <>
                  <Button onClick={handleProcessImage} className="flex-1">
                    <FileImage size={18} className="mr-2" />
                    Processar com OCR
                  </Button>
                  <Button onClick={handleClear} variant="secondary">
                    Cancelar
                  </Button>
                </>
              )}

              {result && (
                <Button onClick={handleClear} className="w-full" variant="secondary">
                  Processar Outra Imagem
                </Button>
              )}
            </div>
          </div>
        )}

        {/* Info */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 text-sm text-blue-800">
          <p className="font-medium mb-1">💡 Dica para melhor resultado:</p>
          <ul className="list-disc list-inside space-y-0.5 text-blue-700">
            <li>Use boa iluminação</li>
            <li>Capture a capa inteira</li>
            <li>Evite reflexos e sombras</li>
            <li>Mantenha a câmera estável</li>
          </ul>
        </div>
      </div>
    </Card>
  );
};

export default ImageUploadOcr;
