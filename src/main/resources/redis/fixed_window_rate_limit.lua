local limit = tonumber(ARGV[1])
local windowSeconds = tonumber(ARGV[2])

local count =redis.call('INCR',KEYS[1])

if count == 1 then 
    redis.call('EXPIRE',KEYS[1],windowSeconds)
end

local ttl =redis.call('TTL',KEYS[1])

if ttl == -1 then 
    redis.call('EXPIRE',KEYS[1],windowSeconds)
    ttl=windowSeconds
end

local allowed =0

if count <= limit then 
    allowed =1;
end

local remaining =math.max(0,limit-count)

return {allowed,remaining,math.max(1,ttl)}