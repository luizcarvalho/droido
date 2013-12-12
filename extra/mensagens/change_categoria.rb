require 'rubygems'
require 'active_record'
require "nokogiri"

ActiveRecord::Base.establish_connection(
    :adapter => "sqlite3",
    :database  => "../../droido.torpedos/src/main/assets/database.sqlite"    
)

class Mensagem < ActiveRecord::Base
    self.table_name = 'mensagens'
    has_many :mensagem_categorias
    has_many :categorias, through: :mensagem_categorias
end

class Categoria < ActiveRecord::Base
    self.table_name = 'categorias'
    has_many :mensagem_categorias
    has_many :mensagens, through: :mensagem_categorias
end

class MensagemCategoria < ActiveRecord::Base
    self.table_name = 'mensagem_categorias'
    belongs_to :categoria
    belongs_to :mensagem
end



def corrigir_categorias()
	manter = [11, 12, 495, 515, 516, 521, 526]
	cat_diversas = Categoria.where(:slug=>"diversas")
	cat_religiosas = Categoria.where(:slug=>"religiosas")
	religiosas = Mensagem.joins(:mensagem_categorias).where("mensagem_categorias.categoria_id"=>cat_religiosas[0].id)
	puts "Total #{religiosas.size}"
	religiosas.each do |mensagem|
		unless(manter.include?(mensagem._id))
			puts mensagem.categorias = [cat_diversas[0]]
		end
	end
end

def add_categorias()
    ids = [328,333, 336, 339, 450]
    cat = Categoria.where(:slug=>"bom-dia")
    
    mensagens = Mensagem.where(ids)
    puts "Total #{mensagens.size}"
    mensagens.each do |mensagem|        
            puts mensagem.categorias#.push(cat)
        end
    end
end


add_categorias
#corrigir_categorias