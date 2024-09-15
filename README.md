# Rcon-Kotlin

[Origin Repo](https://github.com/kr5ch/rkon-core)

## What's different, except language?

Change the class `Rcon` from a util-like class to a socket-like one, so you can:

- Remove `synchronized` keywords
- Use it like a `Reader` / `Writer` or something like that

You can call it in a coroutine scope like this:

```kotlin
withContext(Dispatchers.IO) {
    // calculate UPS for Factorio server
    val ups = Rcon("127.0.0.1", 24197, "my_pw").use {
        val prev = it.command("/sc rcon.print(game.tick)").toFloat()
        delay(60.seconds)
        val now = it.command("/sc rcon.print(game.tick)").toFloat()
        (now - prev) / 60F
    }
}
```

You can move these 3 files to any project you need, or as a dependency.

I hope you enjoy it.
