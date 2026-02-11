import React, { useState, useRef, useEffect } from 'react';
import { MessageCircle, X, Send, Bot, User, Sparkles } from 'lucide-react';
import chatbotService from '../services/chatbotService';
import { ChatMessage, ChatMessageRequest } from '../types/chat';
import Button from './Button';
import Card from './Card';

const ChatWidget: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [sessaoId, setSessaoId] = useState<string | undefined>(undefined);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  // Scroll automático para a última mensagem
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Foco no input ao abrir
  useEffect(() => {
    if (isOpen && inputRef.current) {
      inputRef.current.focus();
    }
  }, [isOpen]);

  // Mensagem de boas-vindas
  useEffect(() => {
    if (isOpen && messages.length === 0) {
      const welcomeMessage: ChatMessage = {
        id: 'welcome',
        role: 'assistant',
        content: 'Olá! 👋 Sou o assistente virtual da biblioteca. Como posso ajudar você hoje?',
        timestamp: new Date(),
      };
      setMessages([welcomeMessage]);
    }
  }, [isOpen]);

  const handleSendMessage = async () => {
    if (!inputMessage.trim() || isLoading) return;

    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      role: 'user',
      content: inputMessage,
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputMessage('');
    setIsLoading(true);

    try {
      const request: ChatMessageRequest = {
        mensagem: inputMessage,
        sessaoId,
      };

      const response = await chatbotService.enviarMensagem(request);

      const assistantMessage: ChatMessage = {
        id: Date.now().toString() + '-response',
        role: 'assistant',
        content: response.resposta,
        timestamp: new Date(response.timestamp),
        livrosSugeridos: response.livrosSugeridos,
      };

      setMessages((prev) => [...prev, assistantMessage]);
      setSessaoId(response.sessaoId);
    } catch (error) {
      console.error('Erro ao enviar mensagem:', error);
      
      const errorMessage: ChatMessage = {
        id: Date.now().toString() + '-error',
        role: 'assistant',
        content: 'Desculpe, ocorreu um erro ao processar sua mensagem. Por favor, tente novamente. 😔',
        timestamp: new Date(),
      };
      
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleClearChat = () => {
    setMessages([]);
    setSessaoId(undefined);
  };

  return (
    <div className="fixed bottom-6 right-6 z-50">
      {/* Botão flutuante */}
      {!isOpen && (
        <button
          onClick={() => setIsOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white rounded-full p-4 shadow-lg transition-all duration-200 hover:scale-110 flex items-center gap-2"
          aria-label="Abrir chat"
        >
          <MessageCircle size={24} />
          <span className="hidden sm:inline font-medium">Assistente IA</span>
        </button>
      )}

      {/* Janela do chat */}
      {isOpen && (
        <Card className="w-96 h-[600px] flex flex-col shadow-2xl animate-fade-in">
          {/* Header */}
          <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-4 rounded-t-lg flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="bg-white/20 p-2 rounded-full">
                <Bot size={20} />
              </div>
              <div>
                <h3 className="font-semibold flex items-center gap-1">
                  Assistente Virtual
                  <Sparkles size={14} className="text-yellow-300" />
                </h3>
                <p className="text-xs text-white/80">Aqui para ajudar 24/7</p>
              </div>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="hover:bg-white/20 p-1 rounded transition-colors"
              aria-label="Fechar chat"
            >
              <X size={20} />
            </button>
          </div>

          {/* Área de mensagens */}
          <div className="flex-1 overflow-y-auto p-4 bg-gray-50 space-y-4">
            {messages.map((message) => (
              <div
                key={message.id}
                className={`flex gap-2 ${
                  message.role === 'user' ? 'justify-end' : 'justify-start'
                }`}
              >
                {message.role === 'assistant' && (
                  <div className="flex-shrink-0 w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                    <Bot size={16} className="text-blue-600" />
                  </div>
                )}
                
                <div
                  className={`max-w-[75%] rounded-2xl px-4 py-2 ${
                    message.role === 'user'
                      ? 'bg-blue-600 text-white rounded-br-none'
                      : 'bg-white text-gray-800 shadow-sm rounded-bl-none'
                  }`}
                >
                  <p className="text-sm whitespace-pre-wrap">{message.content}</p>
                  
                  {/* Livros sugeridos */}
                  {message.livrosSugeridos && message.livrosSugeridos.length > 0 && (
                    <div className="mt-3 space-y-2">
                      <p className="text-xs font-semibold text-gray-600">📚 Sugestões:</p>
                      {message.livrosSugeridos.map((livro) => (
                        <div
                          key={livro.id}
                          className="bg-gray-50 rounded-lg p-2 text-xs border border-gray-200"
                        >
                          <p className="font-semibold text-gray-800">{livro.titulo}</p>
                          <p className="text-gray-600">
                            {livro.nomeAutor} • {livro.nomeCategoria}
                          </p>
                        </div>
                      ))}
                    </div>
                  )}
                  
                  <p className="text-xs opacity-60 mt-1">
                    {new Date(message.timestamp).toLocaleTimeString('pt-BR', {
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                  </p>
                </div>

                {message.role === 'user' && (
                  <div className="flex-shrink-0 w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
                    <User size={16} className="text-white" />
                  </div>
                )}
              </div>
            ))}

            {/* Loading indicator */}
            {isLoading && (
              <div className="flex gap-2 justify-start">
                <div className="flex-shrink-0 w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <Bot size={16} className="text-blue-600" />
                </div>
                <div className="bg-white rounded-2xl rounded-bl-none px-4 py-3 shadow-sm">
                  <div className="flex gap-1">
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0ms' }}></div>
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '150ms' }}></div>
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '300ms' }}></div>
                  </div>
                </div>
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          {/* Input de mensagem */}
          <div className="p-4 bg-white border-t">
            {messages.length > 1 && (
              <button
                onClick={handleClearChat}
                className="text-xs text-gray-500 hover:text-gray-700 mb-2 transition-colors"
              >
                Limpar conversa
              </button>
            )}
            
            <div className="flex gap-2">
              <input
                ref={inputRef}
                type="text"
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="Digite sua mensagem..."
                className="flex-1 px-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
                disabled={isLoading}
              />
              <Button
                onClick={handleSendMessage}
                disabled={!inputMessage.trim() || isLoading}
                className="rounded-full p-2 px-4"
              >
                <Send size={18} />
              </Button>
            </div>
          </div>
        </Card>
      )}
    </div>
  );
};

export default ChatWidget;
