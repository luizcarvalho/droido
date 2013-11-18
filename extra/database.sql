CREATE TABLE `android_metadata` (`locale` text DEFAULT 'en_US');
CREATE TABLE categorias (
  _id INTEGER PRIMARY KEY,
  nome TEXT(32),
  slug TEXT(16)
);
CREATE TABLE log (
  _id INTEGER PRIMARY KEY,
  tipo INTEGER,
  mensagem_slug INTEGER,
  mensagem TEXT,
  user TEXT(64)
);
CREATE TABLE mensagem_categorias (
  _id INTEGER PRIMARY KEY,
  mensagem_id INTEGER,
  categoria_id INTEGER
);
CREATE TABLE "mensagens" ("_id" INTEGER PRIMARY KEY ,"texto" TEXT,"slug" TEXT(16),"avaliacao" INTEGER DEFAULT (2) ,"enviada" INTEGER DEFAULT (0) ,"favoritada" INTEGER DEFAULT (0) ,"data" INTEGER DEFAULT (0) ,"autor" TEXT(64) DEFAULT ('Luiz Carvalho') );
