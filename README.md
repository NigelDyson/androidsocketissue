# androidsocketissue
Android applications for demonstrating slow TCP socket roundtrips


Code supporting a question posted on StackOverflow


I'm having a performance problem with the consistent speed of a tcp socket connection between 2 android tablets talking over WiFi on the same LAN. Occasionally (just enough to be real problem), round-trripping a small payload (< 1400 bytes) between devices can take *many* seconds.  It's not uncommon for it to take **10+ seconds**,  and in the worst case, I've seen a round trip time of **4 minutes.**  The socket is not broken - it's typically blocking on the read operation at the client or server end, and once the round trip finally completes, the next one could take just a few milliseconds.

If I continuously push data through the socket, then I do not see any problems, and it will typically remain fast ( < 100ms) and connected all day long.  However, when I drip feed payloads - say one every couple of seconds, then not only does each round trip take longer, but occasionally, I see these silly times of 10 seconds or more.  I'm not looking for high performance, and anything up to 3 seconds is not really a problem.

I understand that with pauses between payloads, Android will preserve battery by briefly turning off the WiFi radio, and the TCP stack may also add a small delay to give the app layer time to add a bit more data to the payload. These 2 points easily explain the ~120ms rountrips versus the 1-5ms roundtrips when payloads are sent continuously.  However, I've never heard of TCP hanging onto buffered data for 30+ seconds before sending.

This is not the Nagle issue - TCP_NO_DELAY is set to true, and I've played with thread priorities and other TCP tuning flags to no avail.

I'm using 2 Samsung Galaxy Tabs running Lollipop and Marshmallow, though I have also tried Asus, Archos, Lenovo and Huawei devices.

I've invcluded the minimal source of a server and client app that can be used to reproduce the problem for those that like a challenge. Any ideas or help would be greatly appreciated - I'm fast running out of ideas.
