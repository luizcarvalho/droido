require 'rubygems'
require 'active_record'

#ActiveRecord::Base.logger = Logger.new(STDERR)
#ActiveRecord::Base.colorize_logging = true

ActiveRecord::Base.establish_connection(
    :adapter => "sqlite3",
    :database  => "../droido.torpedos/src/main/assets/database.sqlite"
    #:database  => "database.sqlite"
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

def to_ms(time)
        start = Time.new(1970,1,1)
        ((time.to_f - start.to_f) * 1000.0).to_i
end

def create_categorias()
    Categoria.delete_all()
    categorias = [
                    {:id=>1, :nome=>'Diversas',:slug=>'diversas'}, 
                    {:id=>2, :nome=>'Engraçadas',:slug=>'engracadas'}, 
                    {:id=>3, :nome=>'Piadas',:slug=>'piadas'}, 
                    {:id=>4, :nome=>'Amizade',:slug=>'amizade'}, 
                    {:id=>5, :nome=>'Amor',:slug=>'amor'}, 
                    {:id=>6, :nome=>'Religiosas',:slug=>'religiosas'}, 
                    {:id=>7, :nome=>'Frases',:slug=>'frases'}
                ]
    categorias.each do |categoria|
        Categoria.create(:_id=>categoria[:id],:nome=>categoria[:nome],:slug=>categoria[:slug])
    end
    return Categoria.all()
end



def setar_defaults()
    ms = Mensagem.all()
    ms.each do |m|
        m.enviada = false
        m.favoritada = false
        m.save
    end 
end

def set_slug()
    ms = Mensagem.all()
    ms.each do |m|
        m.slug = "#{m.id}.droido"
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

def tmp_text()
	#Mensagem.delete_all()
    categorias = Categoria.all()
    c = 0

	10.times do |i|
        fav = send = 'false'
        if(i>6)
            #fav = 'true'
        end

        if(i>7)
            #send = 'true'
        end
        texto = "AFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA fAFAGA FAGA FAGA FAGA f: #{fav} s:#{send}"
        otexto = "SO OLD f: #{fav} s:#{send}"
		m = Mensagem.create(:avaliacao=>5, :enviada=>send, :favoritada=>fav, :data=>1, :autor=>"Luiz")
        m.slug = "#{m.id}.droido"
        m.texto = "#{texto} -  #{m.slug}"
        m.categorias = [categorias[c]]
		m.save()
        puts m.texto
        c+=1
        c = 0 if c>=3
	end	
end



def create_from_legacy(mlegs)
    Mensagem.delete_all
    count = 0
    goal = 50
    mlegs.each do |ml|
        print "."
        count+=1
        print count; goal+=50 if count > goal
        mensagem = Mensagem.create(
                    :texto=>ml.texto,
                    :avaliacao=>ml.score, 
                    :favoritada=>"false",
                    :enviada=>"false",
                    :data=>to_ms(DateTime.now)                
                     )
        mensagem.slug = "#{mensagem.id}.droid"
        mensagem.categoria_ids = [ml.categoria_id]
        mensagem.save!
    end
    "YEAH"
end


tmp_text