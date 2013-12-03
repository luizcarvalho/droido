require 'rubygems'
require 'active_record'
require "nokogiri"

ActiveRecord::Base.establish_connection(
    :adapter => "sqlite3",
    :database  => "../droido.torpedos/src/main/assets/database.sqlite"    
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

def verify_similar(mensagem, mensagens, file)    
    words = get_words(mensagem.texto)
    
    mensagens.each do |m2|        
        words_capituradas = []
        similar_level = 0
        m2_words = get_words(m2.texto)
        m2_words.each do |m2_word|
            10.times  do |i|
                if words[i] and similar_words(words[i],m2_word)
                    similar_level+=1 
                    words_capituradas.push(m2_word)
                end
            end
        end
        if similar_level>4 and mensagem.id != m2.id
            result = "<tr><td>#{mensagem.id}</td> <td>#{m2.id}</td> <td> \"#{mensagem.texto}\"</td> <td> \"#{m2.texto}\"</td> <td> \"#{words_capituradas.join(',')}\" </td></tr>\n"
            file.write(result)
        end
    end
    
end


def scan()
    mensagens = Mensagem.all()
    puts "Mensagens totais: #{mensagens.size}"
    total = 0
    file = File.open("similar.html", "w+")
    file.puts "<table border='1' >"
    mensagens.each do |mensagem|
        print "#{total+=1} / "
        verify_similar(mensagem, mensagens, file)
    end
    file.puts "</table>"
end

#================================= VERIFICAR DUPLICIDADE ========================





#================================= EXPORTAR =====================================


def export_xml()
    mensagens = Mensagem.all()
    puts "Mensagens totais: #{mensagens.size}"    
    file = File.open("mensagens.xml", "w+")
    file.puts "<mensagens>"
    mensagens.each do |mensagem|        
        file.puts "< mensagem>"
        file.puts "<texto>"
        file.puts mensagem.texto
        file.puts "</texto>"
        file.puts "<slug>"
        file.puts mensagem.slug
        file.puts "</slug>"
        file.puts "</mensagem>"
    end
    file.puts "</mensagens>"
    file.close
    
end

def import_xml
    f = File.open("mensagens_corrigidas.xml")
    doc = Nokogiri::XML(f)
    doc.encoding = 'utf-8'
    msgs = doc.xpath("//mensagem")    
    msgs.each do |msg|
        texto =  msg.at("texto").text
        slug =  msg.at("slug").text
        #puts texto
        puts slug.delete!("\n")
        mensagem = Mensagem.where(:slug=>slug).first
        mensagem.texto = texto
        puts "Editando #{mensagem.slug} => #{mensagem.save}"        
    end
    f.close
end



import_xml






#================================= EXPORTAR =====================================

