require 'active_support/all'
re  = /^(.*) em \d+/

File.foreach("5start.txt") do |line|
	match = line.match(re);
	if match 
		cap =  match.captures[0]
		unless(cap=="Um usuário do Google" or cap=="Você respondeu")
			puts cap.split(/[^a-z0-9]/i).map{|w| w.capitalize}.join(" ")
		end
	end
 end