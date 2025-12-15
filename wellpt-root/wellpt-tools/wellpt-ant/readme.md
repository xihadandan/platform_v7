## 批量修改目录的文件的字符编码
```
	<path id="wellpt-ant.libraryclasspath">
		<fileset dir="C:\wellspace\workspace\wellpt-ant\lib">
			<include name="*.jar" />
		</fileset>
	</path>
	<taskdef name="mycopy" classname="com.wellpt.pt.ant.MyCopy" classpathref="wellpt-ant.libraryclasspath" />
	<target name="file-copy">
		<copy includeemptydirs="false" todir="${dest.dir}">
			<fileset dir="${src.dir}" excludes="**/*.js,**/*.css,**/*.xml,**/*.html">
			</fileset>
		</copy>
		<echo>file-copy success</echo>
	</target>
	<target name="file-change">
		<mycopy includeemptydirs="false" todir="${dest.dir}" encoding="auto" outputencoding="UTF-8">
			<fileset dir="${src.dir}" includes="**/*.js,**/*.css,**/*.xml,**/*.html">
			</fileset>
		</mycopy>
		<echo>file-change success</echo>
	</target>
	<target name="file-copy-change-encoding" depends="clean,init,file-change,file-copy">
		<echo>file-copy-change-encoding success</echo>
	</target>
```
### Maven导出依赖
```
mvn dependency:copy-dependencies -DoutputDirectory=lib
```