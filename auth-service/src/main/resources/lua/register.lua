if redis.call("EXISTS", KEYS[1]) ~= 1 then
  redis.call("set", KEYS[1], ARGV[1])
  redis.call("expire", KEYS[1], tonumber(ARGV[2]))
  return '0';
else
  return redis.call("get", KEYS[1])
end