TX == tx == a transmitter or sender, usually of a message or comm (or used as a verb)
RX == rx == a receiver, usually of a message or comm (or used as a verb)


Model
	This package contains the Team, which acts as the shared group all
	of the roles belong to while the model is running. It provides access
	to the Team members (the various Roles), and an ability to start the Team
	and see if the team is still working.

Role
	This package contains each of the Roles/workers for this model. Each inherits
	from Person, which gives some shared basic functionality. Each Role is basically
	A worker who works on whatever is in their message, observation, and video
	queues. Person inherits from the Java Thread class so they can be told to
	go work. Also, when the Environment is done sending out observations (which
	serve as the catalyst for interactions) it begins a passing around of a check
	to see if everyone is done. If the check gets back to the Environment and == 0
	that indicates the sent and received messages recorded is equal and so everyone
	should be done (a sort of Djikstra's Termination token algorithm).

Simulation
	This package is used to generate lists of events for the Environment to
	send to get the model running.

Util
	Just some utilities

Vocab
	These are the various vocabularies that are used to comm between roles.
	They are defined by the rx.


NOTE: Their may be some non-determinism in the model's behavior, since who gets what
message in what order affects what happens in some cases, and since these workers are
all independent, async entities this can for example make test 3 in TestRun.java fail,
depending on if delays are on (Timing.java) or how quickly the Environment is firing off
observations. Failure of the test *may* simply be this non-deterministic behavior, 
however this is speculation and has not been thoroughly tested/confirmed.