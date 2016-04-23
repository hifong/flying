
	<bean id="{$ClassName}DAO" class="{$PackageName}.dao.{$ClassName}DAO" parent="baseDao" />

	<bean id="{$ClassName}Service" parent="baseTransactionProxy">
		<property name="transactionManager">
			<ref bean="transactionManager"/>
		</property>
		<property name="target">
			<bean class="{$PackageName}.service.impl.{$ClassName}ServiceImpl">
				<property name="{$ClassName | asprop}DAO">
					<ref bean="{$ClassName}DAO"/>
				</property>
			</bean>
		</property>
	</bean>
