Cloud Registry Java EPP-RTK Add-on
==================================

This is a collection of Java classes that complements the [Universal Registry/Registrar Toolkit][1]
providing functionalities specific to Cloud Registry's extension to the [Extensible Provisioning Protocol (EPP)][2].


Requirements
------------
* JDK 1.5+
* epp-rtk-java 0.9.6


Getting Started
---------------
1. Set the RTK_HOME environment variable to the directory where you unpacked epp-rtk-java distribution.

<pre><code>export RTK_HOME=&lt;path to epp-rtk&gt;</code></pre>


2. Run the example code.

<pre><code>./run_example.sh epp_host_name epp_host_port epp_client_id epp_password domain_name contact_id</code></pre>



Sample Code
-----------
The ``run_example.sh`` script above executes the runnable class ``net.cloudregistry.rtk.epprtk.LaunchPhaseTest``
which shows how the library can be used in conjunction with the EPP-RTK. You are encouraged to follow the sample
to develop your application.




[1]: http://sourceforge.net/projects/epp-rtk/  "Universal Registry/Registrar Toolkit"
[2]: http://tools.ietf.org/html/rfc5730        "EPP"
