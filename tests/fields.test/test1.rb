class A
  def initialize
    @x = 42
    @y = @x
  end
  attr_accessor :x
end

o1 = A.new
puts o1.x
