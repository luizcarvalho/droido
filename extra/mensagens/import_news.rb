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


#================================= VERIFICAR DUPLICIDADE ========================

def get_words(mensagem_texto)
    mensagem_texto.scan(/[\w-]+/)
end

def similar_words(m1, m2)
    similar = false
    if(m1 and m2)
        if(m1.size > 4 and m2.size > 4)
            similar = true if m1==m2
        end
    else
        puts "ERROR m1: #{m1}  /  m2: #{m2}"
    end

    similar
end

def verify_similar(mensagem, file, index)
    mensagens = Mensagem.all()
    tex  = mensagem.at("texto").text
    words = get_words(tex)
    
    
    mensagens.each do |m2|

        words_capituradas = []
        similar_level = 0
        m2_words = get_words(m2.texto)
        m2_words.each do |m2_word|
            15.times  do |i|
                if words[i] and similar_words(words[i],m2_word)
                    similar_level+=1 
                    words_capituradas.push(m2_word)
                end
            end
        end
        if similar_level>4            
            result = "<tr> <td>#{index} <td> <td>#{m2.id}</td>  <td> \"#{tex}\"</td> <td> \"#{m2.texto}\"</td> <td> \"#{words_capituradas.join(',')}\" </td></tr>\n"
            file.write(result)
        end
    end
    
end



def import_xml
    f = File.open("engracadas.xml")
    doc = Nokogiri::XML(f)
    doc.encoding = 'utf-8'
    msgs = doc.xpath("//mensagem")    
    f.close
    msgs
end


def scan()
    mensagens = import_xml
    puts "Mensagens totais: #{mensagens.size}"
    total = 0
    
    file = File.open("similar.html", "w+")
    file.puts "<table border='1' >"
    mensagens.each_with_index do |mensagem,  i|
        print "#{total+=1} / "        
        verify_similar(mensagem, file, i)
    end
    file.puts "</table>"
end


#================================= VERIFICAR DUPLICIDADE ========================


#================================= IMPORTAR NOVAS ========================

def to_ms()
        time = Time.now
        start = Time.new(1970,1,1)
        ((time.to_f - start.to_f) * 1000.0).to_i
end

def new_autor(index)
    max= 48
    if(index>=max)
        index=index-max
    end
    IO.readlines("5star.txt")[index]
end



def create_new
    novas = import_xml
    puts "Total #{novas.size}"
    novas.each_with_index do |msg, i|
        texto  = msg.at("texto").text
        autor  = msg.at("autor").text
        avaliacao = msg.at("avaliacao").text
        mensagem = Mensagem.create(:texto=>texto, :autor=>autor, :avaliacao=>avaliacao, :data=>to_ms, :enviada=>"false", :favoritada=>"false")
        mensagem.save
        mensagem.slug = "#{mensagem.id}.droid"
        print "#{i} / "
        msg.children.children[3].text.split(",").each do |cat|            
            categoria = Categoria.where(:slug=>cat.delete(" "))
            unless(categoria)
                puts "Categoria não encontrada"
            else
                mensagem.categorias.push(categoria)
            end
            
        end
        mensagem.save

    end
end

scan
#create_new


