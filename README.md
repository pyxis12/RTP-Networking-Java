# RTP-Networking-Java
Using UDP Sockets to mimic TCP

Milestones

	) Implement lost packet detection & duplicate - timeouts (sat) - Andrea
		send - timeout and resend
		receive - make ack no. (which will probably be done in flow crt) and check dupes
		
	) Implement package corruption detection - checksum (sat) - Andrea
		at receieve
		
	) Implement package re-ordering detection - the sequence and ack number thing (sun) - Jeffer
		ackno = seqno+packload
		
	) Implement Congestion control - RTT and slow start and ssthresh stuff (mon) - Jeffer

	) Office hours (tue) - 1:30pm-3pm

	) Write FTA (by tue) - Andrea
		
Ask the TA:
	What happens hen we run out of sequence numbers? (2g file?)
	In connection establishment, should we time-out packets? What about using TCP's ETT?
	Where to start ssthresh (half of recieve window, maybe)
