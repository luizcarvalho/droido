require 'rubygems'
require 'active_record'
require "nokogiri"

ActiveRecord::Base.establish_connection(
    :adapter => "sqlite3",
    :database  => "mensagenspracelular.db"    
)

class Mensagem < ActiveRecord::Base
    self.table_name = 'messages'    
    belongs_to :categorias
end

class Categoria < ActiveRecord::Base
    self.table_name = 'categories'    
    has_many :mensagens
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
    mensagens = Mensagem.where(:category_id=>45)
    puts "Mensagens totais: #{mensagens.size}"    
    file = File.open("mensagens.xml", "w+")
    file.puts "<mensagens>"
    mensagens.each do |mensagem|        
        file.puts "<mensagem>"
        file.puts "<texto>"
        file.puts mensagem.message
        file.puts "</texto>"
        file.puts "<autor>\n[Toque para colocar seu nome]\n</autor>\n"
        file.puts "<avaliacao>\n30\n</avaliacao>\n"
        file.puts "<categorias>\n\t<categoria>desenhos</categoria>\n</categorias>\n"
        file.puts "</mensagem>\n\n\n\n"
    end
    file.puts "</mensagens>"
    file.close
    
end


def reset_autor
    mensagens = Mensagem.all()
    mensagens.each do |mensagem|
        if(mensagem.autor=="Luiz Carvalho")
            puts mensagem.autor="[Coloque seu nome aqui]"
            mensagem.save
        end
    end

end

export_xml


#import_xml








#================================= EXPORTAR =====================================

