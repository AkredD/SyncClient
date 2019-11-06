# SyncClient

  This application is used to configure file synchronization.

## Start

  Make sure you have java version greater or equals 11
In order to start, you need to create a provider, and then configure the transfer job.
After that, you can start it and watch progress from the main menu.

## Work cases

### Providers
   You can create local or ssh provider to read or write files for synchronization.
 If you want to user ssh provider, make sure the public key is in the destination server,
 otherwise you couldn't connect.

### Transfers

  Using your providers, you can create transfer job. Just select `from` and `to` providers and enter pathes.
After that, you can run it from the main menu.

### Main menu

  In the main menu you can see transfer info and control it.
There are some transfers information statuses
 1. synchronizing - transfer is running
 2. scheduling - waiting(1 minuite default)
 3. stopped - transfer interrupted
 4. stopped(provider closed) - one of providers is disconnected and you need to connect it manually

## Build

- Clone repository
- Set up jdk 11 or greater
- Build it with maven



