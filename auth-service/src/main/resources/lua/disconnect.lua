local value = redis.call("get", KEYS[1])
if value ~= nil and value == ARGV[1] then
   redis.call("del", KEYS[1])
   return true;
else
   return false;
end