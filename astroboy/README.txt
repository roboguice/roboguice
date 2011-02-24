This sample application was built using Eclipse.

Caveats
- Eclipse seems to be having some problems when both the
  RoboGuice and astroboy projects are open.  You may
  get various errors and warnings because astroboy is
  a subdir of roboguice, but they seem to be ignorable.
- You will probably have to update the astroboy's reference
  to the guice libraries.  Go to Project Properties ->
  Java Build Path -> Libraries and make sure it's resolvable

