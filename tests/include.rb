#!/usr/bin/ruby
require "/Users/yinwang/Dropbox/prog/pysonar2/tests/ruby/support"

class Decade
  include Week
  no_of_yrs=10

  def no_of_months
    puts Week::FIRST_DAY
    number=10*12
    puts number
  end
end
d1=Decade.new
puts Week::FIRST_DAY
Week.weeks_in_month
Week.weeks_in_year
d1.no_of_months

puts "before end"

def f
  x = 1
  END {
    puts "end: #{x}"
  }
end

f
x = 2
puts "after end: #{x}"

def f(y)
  lambda {|x| x+y}
end

puts f(2).(3)
