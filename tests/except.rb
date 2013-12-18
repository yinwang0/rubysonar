#def raise_exception
#  puts 'I am before the raise.'
#  raise 'An error has occured'
#  puts 'I am after the raise'
#end
#raise_exception


#class A < StandardError
#end
#
#class B < StandardError
#end
#
#class C < StandardError
#  def initialize(x)
#
#  end
#end


#
#def inverse(x)
#  raise A, 'blah'
#  1.0 / x
#end
#puts inverse(2)
#puts inverse('not a number')


def raise_and_rescue
  begin
    puts 'I am before the raise.'
    raise C 'blah'
    puts 'I am after the raise.'
  rescue A,B => e
    puts 'rescuing A,B'
  rescue C
    puts 'rescuing C'
  else
    puts 'else clause'
  ensure
    puts 'ensured block'
  end
  puts 'I am after the begin block.'
end
#
#raise_and_rescue


#begin
#  File.open('p014constructs.rb', 'r') do |f1|
#    while line = f1.gets
#      puts line
#    end
#  end
#
#  # Create a new file and write to it
#  File.open('test.rb', 'w') do |f2|
#    # use "" for two lines of text
#    f2.puts "Created by Satish\nThank God!"
#  end
#rescue Exception => msg
#  # display the system generated error message
#  puts msg
#end
