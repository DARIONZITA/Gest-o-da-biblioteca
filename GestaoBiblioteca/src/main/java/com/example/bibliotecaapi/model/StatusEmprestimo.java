package com.example.bibliotecaapi.model;

public enum StatusEmprestimo {
    /**
     * Solicitação criada pelo membro e aguardando aprovação do admin.
     * Neste estado, a quantidadeDisponivel do livro ainda nao foi alterada.
     */
    PENDENTE,

    /**
     * O empréstimo foi realizado e o livro está com o usuário.
     * Neste estado, a quantidadeDisponivel do livro já foi subtraída.
     */
    ATIVO,

    /**
     * O livro foi entregue dentro do prazo ou com atraso já resolvido.
     * Ao mudar para este status, a quantidadeDisponivel deve aumentar (+1).
     */
    DEVOLVIDO,

    /**
     * O prazo expirou e o usuário ainda não entregou.
     * Útil para filtrar usuários que não podem fazer novos empréstimos.
     */
    ATRASADO,




}
