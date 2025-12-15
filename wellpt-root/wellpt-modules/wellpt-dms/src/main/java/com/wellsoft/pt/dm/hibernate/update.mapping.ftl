<hibernate-mapping>
    <class name="${tableName}" table="${tableName}" <#if schema??> schema="${schema}" </#if> >
        <#if comment??>
            <comment>${comment}</comment></#if>
        <id name="${id.name}" type="${id.type}">
            <column name="${id.name}" unique="false" <#if id.length??> length="${id.length?c}" </#if> />
        </id>

        <#list propertyList as prop>
            <property name="${prop.name}" type="${prop.type}">
                <column name="${prop.name}" not-null="${prop.notNull?c}" unique="${prop.unique?c}"
                        <#if prop.length??> length="${prop.length?c}" </#if>
                        <#if prop.default??> default="${prop.default}" </#if>
                        <#if prop.precision??> precision="${prop.precision}" </#if>
                        <#if prop.scale??> scale="${prop.scale}" </#if>
                        <#if prop.uniqueKey??> unique-key="${prop.uniqueKey}" </#if>
                >

                    <#if prop.rename??>
                        <comment>{"rename":"${prop.rename}" <#if prop.comment??>,"comment":"${prop.comment}" </#if>}
                        </comment>
                    <#else>
                        <#if prop.comment??>
                            <comment>${prop.comment}</comment> </#if>
                    </#if>
                </column>
            </property>
        </#list>


    </class>
</hibernate-mapping>