def foo(f, &block)
  block(10)
end


# foo { |x| x }


def bar(&block)
  foo(42, &block)
end

bar { |x| x }
