require 'rubygems'
require 'active_record'
require 'date'

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

class MensagemLegacy < ActiveRecord::Base
    self.table_name = 'textos'
    has_many :categorias    
end

class CategoriaLegacy < ActiveRecord::Base
    self.table_name = 'categorias'
    belongs_to :mensagem    
end



def to_ms(time)
        start = Time.new(1970,1,1)
        ((time.to_f - start.to_f) * 1000.0).to_i
end

