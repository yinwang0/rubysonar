module A
   def a1
     puts 'a1'
   end
end

class Sample
include A
   def s1
     puts 's1'
   end
end

samp=Sample.new
samp.a1
samp.s1
