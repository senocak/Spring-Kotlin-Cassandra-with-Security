package com.github.senocak.skc.domain

import org.springframework.context.ApplicationEvent

class UserEmailActivationSendEvent(source: Any, val user: User) : ApplicationEvent(source)
