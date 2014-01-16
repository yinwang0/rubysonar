def foo(f, &block)
  block(10)
end

foo { |x| x }
