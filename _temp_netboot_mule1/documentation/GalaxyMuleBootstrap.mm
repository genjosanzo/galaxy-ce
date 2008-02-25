<map version="0.9.0_Beta_8">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1201708840126" ID="Freemind_Link_1852322021" MODIFIED="1201708855735" TEXT="Mule Galaxy Bootstrap">
<node CREATED="1201708856860" ID="Freemind_Link_1043188714" MODIFIED="1201708862657" POSITION="right" TEXT="Galaxy">
<node CREATED="1201708901985" ID="Freemind_Link_520627178" MODIFIED="1201711703735" TEXT="Preparation phase">
<node CREATED="1201708918251" ID="Freemind_Link_782224188" MODIFIED="1201708929907" TEXT="Create workspaces">
<node CREATED="1201708930579" HGAP="29" ID="Freemind_Link_772629301" MODIFIED="1201711901329" TEXT="Store Mule jars and system configs" VSHIFT="-64">
<node CREATED="1201710148891" ID="Freemind_Link_1552682708" LINK="http://mule.mulesource.org/jira/browse/GALAXY-76" MODIFIED="1201710253813" TEXT="Winstone is terribly slow for upload">
<icon BUILTIN="flag"/>
<node CREATED="1201710172032" ID="Freemind_Link_1666042609" MODIFIED="1201710179423" TEXT="WAR deployed to Tomcat 6 rocks"/>
</node>
</node>
<node CREATED="1201708940329" HGAP="31" ID="Freemind_Link_1011042748" MODIFIED="1201711902704" TEXT="Mirror $MULE_HOME/lib" VSHIFT="-40">
<node CREATED="1201710123313" ID="Freemind_Link_1986416946" MODIFIED="1201710140032" TEXT="Not yet sure about non-jar resources">
<icon BUILTIN="help"/>
</node>
</node>
<node CREATED="1201708961032" ID="Freemind_Link_1901316676" LINK="http://www.mulesource.org/jira/browse/GALAXY-77" MODIFIED="1201709992657" TEXT="Can&apos;t list workspace contents">
<icon BUILTIN="messagebox_warning"/>
<node CREATED="1201709055266" ID="Freemind_Link_66168746" MODIFIED="1201709067126" TEXT="Maybe can be replaced with search">
<icon BUILTIN="help"/>
<node CREATED="1201709071719" ID="Freemind_Link_973288374" LINK="http://www.mulesource.org/jira/browse/GALAXY-75" MODIFIED="1201709124001" TEXT="Doesn&apos;t work in Web UI">
<icon BUILTIN="messagebox_warning"/>
</node>
</node>
</node>
<node CREATED="1201709002173" HGAP="26" ID="Freemind_Link_1589510620" LINK="http://www.mulesource.org/jira/browse/GALAXY-78" MODIFIED="1201711904423" TEXT="No Service Document URL" VSHIFT="81">
<icon BUILTIN="messagebox_warning"/>
<node CREATED="1201709014157" ID="Freemind_Link_795803544" MODIFIED="1201709039001">
<richcontent TYPE="NODE"><html>
  <head>
    <style type="text/css">
      <!--
        p { margin-top: 0 }
      -->
    </style>
    
  </head>
  <body>
    <p>
      Service Doc URL is not a strict
    </p>
    <p>
      definition in APP
    </p>
  </body>
</html>
</richcontent>
</node>
<node CREATED="1201709044407" HGAP="28" ID="Freemind_Link_1880835819" MODIFIED="1201711905969" TEXT="This is application-specific" VSHIFT="14"/>
</node>
</node>
<node CREATED="1201710297501" HGAP="-2" ID="Freemind_Link_1353893801" LINK="http://www.mulesource.org/jira/browse/GALAXY-72" MODIFIED="1201711897454" TEXT="Dedicated JAR indexers needed" VSHIFT="97">
<node CREATED="1201710365829" ID="Freemind_Link_1005649007" MODIFIED="1201710370360" TEXT="Locked properties">
<node CREATED="1201710371094" ID="Freemind_Link_1480424697" MODIFIED="1201710381657" TEXT="Data from MANIFEST.MF"/>
<node CREATED="1201710386485" ID="Freemind_Link_1983965418" MODIFIED="1201711259782" TEXT="Updater/Last Modified By User"/>
<node CREATED="1201710407391" ID="Freemind_Link_1556436692" MODIFIED="1201710878016" TEXT="Checksum">
<icon BUILTIN="idea"/>
<node CREATED="1201710443485" ID="Freemind_Link_638021418" MODIFIED="1201710455157" TEXT="Prevent corrupted downloads by Mule"/>
<node CREATED="1201710458313" ID="Freemind_Link_241301031" MODIFIED="1201710467891" TEXT="Possible performance hit">
<node CREATED="1201710469360" ID="Freemind_Link_1094800905" MODIFIED="1201710521048" TEXT="Use checksum input/output streams">
<icon BUILTIN="button_ok"/>
<node CREATED="1201710524610" ID="Freemind_Link_30254414" LINK="http://java.sun.com/j2se/1.4.2/docs/api/java/util/zip/CheckedInputStream.html" MODIFIED="1201710560610" TEXT="OOTB in JDK">
<node CREATED="1201710539079" ID="Freemind_Link_1977675502" MODIFIED="1201710541298" TEXT="CRC32"/>
<node CREATED="1201710542282" ID="Freemind_Link_1010849296" MODIFIED="1201710549360" TEXT="Adler32"/>
</node>
<node CREATED="1201710562548" ID="Freemind_Link_61182513" MODIFIED="1201710573548" TEXT="3rd-party">
<node CREATED="1201710574282" ID="Freemind_Link_782701866" MODIFIED="1201710576876" TEXT="MD5"/>
<node CREATED="1201710577657" ID="Freemind_Link_1312672844" MODIFIED="1201710579157" TEXT="HASH"/>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
<node CREATED="1201708863485" HGAP="41" ID="Freemind_Link_1016468197" MODIFIED="1201711930719" POSITION="left" TEXT="Mule" VSHIFT="-2">
<node CREATED="1201708865751" HGAP="47" ID="Freemind_Link_490275541" MODIFIED="1201711888282" TEXT="A new tiny distribution of Mule&#xa;which can be bootstrapped from Galaxy" VSHIFT="-97"/>
<node CREATED="1201710600313" HGAP="99" ID="Freemind_Link_1906519818" MODIFIED="1201710767501" TEXT="GalaxyConfigBuilder" VSHIFT="-48"/>
<node CREATED="1201710613501" HGAP="87" ID="Freemind_Link_219154508" MODIFIED="1201711587032" TEXT="GalaxyMuleBootstrap" VSHIFT="28">
<node CREATED="1201710721157" ID="Freemind_Link_974677442" MODIFIED="1201710767501" TEXT="Try to reuse the config builder">
<arrowlink DESTINATION="Freemind_Link_1906519818" ENDARROW="Default" ENDINCLINATION="-26;58;" ID="Freemind_Arrow_Link_1717659724" STARTARROW="None" STARTINCLINATION="-19;-37;"/>
</node>
<node CREATED="1201710626032" HGAP="33" ID="Freemind_Link_380311183" MODIFIED="1201710770235" TEXT="Requires replacing a value in wrapper.conf" VSHIFT="5">
<node CREATED="1201710678298" ID="Freemind_Link_266059634" MODIFIED="1201711888282" TEXT="Alternative Mule distribution">
<arrowlink DESTINATION="Freemind_Link_490275541" ENDARROW="Default" ENDINCLINATION="327;49;" ID="Freemind_Arrow_Link_528612427" STARTARROW="None" STARTINCLINATION="4;-58;"/>
</node>
<node CREATED="1201710660954" ID="Freemind_Link_756524708" MODIFIED="1201710676579" TEXT="Manual">
<icon BUILTIN="smily_bad"/>
</node>
</node>
<node CREATED="1201711383485" ID="Freemind_Link_942368252" MODIFIED="1201711433844" TEXT="Provided with URL[] directly">
<node CREATED="1201711409688" HGAP="25" ID="Freemind_Link_849998840" MODIFIED="1201711480157" TEXT="Need to test if this is performant enough" VSHIFT="28">
<arrowlink DESTINATION="Freemind_Link_71101025" ENDARROW="Default" ENDINCLINATION="26;-67;" ID="Freemind_Arrow_Link_1479559759" STARTARROW="None" STARTINCLINATION="-47;12;"/>
<icon BUILTIN="flag"/>
</node>
</node>
<node CREATED="1201710746079" HGAP="21" ID="Freemind_Link_1003353319" MODIFIED="1201710869079" TEXT="Use it for resources only" VSHIFT="127">
<node CREATED="1201710798063" ID="Freemind_Link_71101025" MODIFIED="1201711480157" TEXT="Local caches">
<node CREATED="1201710883485" ID="Freemind_Link_1440928214" MODIFIED="1201711259782" TEXT="Check for updates">
<arrowlink DESTINATION="Freemind_Link_1983965418" ENDARROW="Default" ENDINCLINATION="-2;119;" ID="Freemind_Arrow_Link_318618993" STARTARROW="None" STARTINCLINATION="-85;-66;"/>
</node>
<node CREATED="1201710829969" ID="Freemind_Link_445964563" MODIFIED="1201710878016" TEXT="Validate checksum">
<arrowlink DESTINATION="Freemind_Link_1556436692" ENDARROW="Default" ENDINCLINATION="-194;279;" ID="Freemind_Arrow_Link_1012553882" STARTARROW="None" STARTINCLINATION="-68;48;"/>
</node>
</node>
</node>
<node CREATED="1201711562563" ID="Freemind_Link_615059151" MODIFIED="1201711589876" TEXT="Would it work with all of the abdera client jars thrown in?">
<icon BUILTIN="clanbomber"/>
</node>
</node>
</node>
</node>
</map>
