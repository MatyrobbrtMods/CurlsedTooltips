ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[47,)'

    license = 'MIT'
    // A URL to refer people to when problems occur with this mod
    // issueTrackerUrl = 'https://change.me.to.your.issue.tracker.example.invalid/'

    mod {
        modId = 'curlsedtooltips'
        displayName = 'Curlsed Tooltips'
		displayTest = 'IGNORE_ALL_VERSION'

        version = this.version

        description = '''A mod adding cursed scrollable tooltips to Minecraft.'''
        authors = ['Matyrobbrt']

        // logoFile = 'curlsedtooltips.png'

        dependencies {
            forge = "[${this.forgeVersion},)"
            minecraft = this.minecraftVersionRange
        }
    }
}