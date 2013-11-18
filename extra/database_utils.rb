require 'rubygems'
require 'active_record'

#ActiveRecord::Base.logger = Logger.new(STDERR)
#ActiveRecord::Base.colorize_logging = true

ActiveRecord::Base.establish_connection(
    :adapter => "sqlite3",
    #:database  => "../droido.torpedos/src/main/assets/database.sqlite"
    :database  => "database.sqlite"
)
=begin
ActiveRecord::Schema.define do
    create_table :mensagens, {id: false, :primary_key => :_id } do |table|
        table.column :_id, :integer
        table.column :texto, :text
        table.column :slug, :string
        table.column :nome_autor, :string
        table.column :faviritada, :boolean
        table.column :enviada, :boolean
        table.column :data, :datetime
    end

    create_table :categorias, { :primary_key => :_id } do |table|
        table.column :_id, :integer
        table.column :nome, :string
        table.column :slug, :string
    end

    create_table :menssagem_categorias, { :primary_key => :_id } do |table|
        table.column :_id, :integer
        table.column :categoria_id, :integer
        table.column :mensagem_id, :integer
    end
end
=end

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





def setar_defaults()
    ms = Mensagem.all()
    ms.each do |m|
        m.enviada = false
        m.favoritada = false
        m.save
    end 
end


def set_autor()
    ms = Mensagem.all()
    autores = ["Luiz Carvalho", "RedRails Dev", "Droido App", "Luiz Carvalho", "Luiz Carvalho", "Luiz Carvalho"]
i=0
max = 5
    ms.each do |m|
	m.autor = autores[i]
        m.save
	i+=1
	i=0 if i> 5
    end 
end



def categorizar_mensagens()
    ms = Mensagem.all()
    ms.each do |m|
        MensagemCategoria.create(:categoria_id=>m.categoria_id, :mensagem_id=>m.id)
    end 
end

def tmp_text(num)
	Mensagem.delete_all()
	20.times do |i|
		m = Mensagem.create(:texto=>"texto #{num}",:categoria_id=>1, :avaliacao=>3, :enviada=>"true", :favoritada=>"true", :data=>1, :autor=>"LC")
		m.slug = "#{m.id}.droido"
		m.save()
	end
	
end




tmp_text("bem louco")

