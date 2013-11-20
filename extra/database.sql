CREATE TABLE `android_metadata` (`locale` text DEFAULT 'pt_BR');
CREATE TABLE categorias (
  _id INTEGER PRIMARY KEY,
  nome TEXT(32),
  slug TEXT(16)
);
CREATE TABLE log (
  _id INTEGER PRIMARY KEY,
  tipo INTEGER,
  mensagem_slug TEXT(16),
  mensagem TEXT,
  user TEXT(64)
);
CREATE TABLE mensagem_categorias (
  _id INTEGER PRIMARY KEY,
  mensagem_id INTEGER,
  categoria_id INTEGER
);

CREATE TABLE "mensagens" (
  "_id" INTEGER PRIMARY KEY ,
  "texto" TEXT,
  "slug" TEXT(16),
  "avaliacao" REAL DEFAULT (2.5) ,
  "enviada" INTEGER DEFAULT (0) ,
  "favoritada" INTEGER DEFAULT (0) ,
  "data" INTEGER DEFAULT (0) ,
  "autor" TEXT(64) DEFAULT ('Luiz Carvalho') 
);



-- INICIAR TRANSAÇÃO
-- Extrair Dados
SELECT * FROM categorias_temp 
SELECT * FROM mensagem_categorias_temp
SELECT * FROM mensagens_temp

-- deletarTabela()
DROP TABLE IF EXISTS mensagens_temp
DROP TABLE IF EXISTS mensagem_categorias_temp
DROP TABLE IF EXISTS categorias

-- criar TabelaTemporaria()
CREATE TABLE categorias_temp (
  _id INTEGER PRIMARY KEY,
  nome TEXT(32),
  slug TEXT(16)
);
CREATE TABLE mensagem_categorias_temp (
  _id INTEGER PRIMARY KEY,
  mensagem_id INTEGER,
  categoria_id INTEGER
);

CREATE TABLE mensagens_temp (
  "_id" INTEGER PRIMARY KEY ,
  "texto" TEXT,
  "slug" TEXT(16),
  "avaliacao" REAL DEFAULT (2.5) ,
  "enviada" INTEGER DEFAULT (0) ,
  "favoritada" INTEGER DEFAULT (0) ,
  "data" INTEGER DEFAULT (0) ,
  "autor" TEXT(64) DEFAULT ('Luiz Carvalho') 
);

-- Importar dados do XML



-- fazerBackup()
INSERT INTO mensagens_temp ( texto,favoritada,enviada )
SELECT texto,favoritada,enviada FROM mensagens  LEFT JOIN mensagem_categorias ON mensagem_categorias.mensagem_id = mensagens._id   WHERE favoritada='true' OR enviada='true' OR mensagem_categorias.categoria_id =3

-- copiarDados()

-- restaurarBackup()

-- deletarTabelaTemporaria()


