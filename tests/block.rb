obj.each do |k, v|
  if k == :location
    start_idx = node_start(v)
    end_idx = ident_end(start_idx)
    new_hash[:start] = start_idx
    new_hash[:end] = end_idx
  else
    new_hash[k] = convert_locations(v)
  end
end

