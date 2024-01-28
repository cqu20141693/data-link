local value = redis.call("get", KEYS[1])
if (value == nil or (type(value) == 'boolean' and  not value)) then
   redis.call("set", KEYS[1], ARGV[1])
   redis.call("expire", KEYS[1], tonumber(ARGV[2]))
   return 1;
else
   if value == ARGV[1] then
      redis.call("expire", KEYS[1], tonumber(ARGV[2]))
      return 0;
   else
      return -1;
   end
end