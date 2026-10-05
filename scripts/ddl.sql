-- Azure SQL Server. Rodar no banco dimdim depois de criar o servidor e o database.
-- Duas tabelas com relacionamento 1:N (conta -> transacao).

IF OBJECT_ID('dbo.transacao', 'U') IS NOT NULL DROP TABLE dbo.transacao;
IF OBJECT_ID('dbo.conta', 'U') IS NOT NULL DROP TABLE dbo.conta;
GO

CREATE TABLE dbo.conta (
    id    INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    nome  NVARCHAR(100)     NOT NULL,
    tipo  NVARCHAR(50)      NOT NULL,
    saldo DECIMAL(18,2)     NOT NULL CONSTRAINT DF_conta_saldo DEFAULT (0)
);
GO

CREATE TABLE dbo.transacao (
    id             INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    conta_id       INT               NOT NULL,
    descricao      NVARCHAR(200)     NOT NULL,
    valor          DECIMAL(18,2)     NOT NULL,
    tipo           NVARCHAR(20)      NOT NULL,
    data_transacao DATETIME2         NOT NULL CONSTRAINT DF_transacao_data DEFAULT (SYSUTCDATETIME()),
    CONSTRAINT FK_transacao_conta FOREIGN KEY (conta_id) REFERENCES dbo.conta(id)
);
GO
