# doc-builder-plugin

```xml
<pluginRepositories>
	<pluginRepository>
		<id>jtool-mvn-repository</id>
		<url>https://raw.github.com/JavaServerGroup/jtool-mvn-repository/master/releases</url>
	</pluginRepository>
	<pluginRepository>
		<id>jtool-mvn-snapshots</id>
		<url>https://raw.github.com/JavaServerGroup/jtool-mvn-snapshots/master/snapshots</url>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
	</pluginRepository>
</pluginRepositories>
```


```xml
<plugins>
	<plugin>
		<groupId>com.jtool</groupId>
		<artifactId>doc-builder-plugin</artifactId>
		<version>0.0.1</version>
		<configuration>
			<packageName>com.palmchat.mediaServer.controller</packageName>
			<fileName>broadcastApi.html</fileName>
		</configuration>
	</plugin>
</plugins>
```

compile com.jtool:doc-builder-plugin:build -X

