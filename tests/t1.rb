#x = 1.6

#def f(a, b, c=5, *p, q)
#  q
#end
#
#
#y = f(1, 2, 3, 4, 5, 6, 7, 8) {|x| x}
#puts y


#def f2(&b)
#  b
#end
#
#
#y = f2 {|x| x}
#z = y(1)
#puts y, z



#def f(x, y=2000, z)
#  puts "x=#{x}, y=#{y}, z=#{z}"
#  x+y+z
#end
#
#w = f(1, 2)
#
#if w > 100
#  u = 'big'
#else
#  u = 'small'
#end
#
#puts u


#
#y = f
#
#if y > 3
#  z = 'hi'
#else
#  z = 42
#end
#
#puts z


#for x in y
#  puts x
#end

#class B
#end
#
#class A < B
#end
#
#y=1
#x = A.new(y+1)


#begin
#  x = 1
#  y = f(x)
#rescue e1
#  z = 'e1'
#rescue e2
#  z = 'e2'
#else
#  z = 'else'
#ensure
#  w = 5
#end
#
#puts w


class A
  def f
    puts self
  end
end

puts $LOAD_PATH
