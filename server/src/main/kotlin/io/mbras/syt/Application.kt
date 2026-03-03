package io.mbras.syt

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.mbras.syt.missions.routes.missionsRoutes
import io.mbras.syt.missions.routes.authRoutes
import io.mbras.syt.missions.routes.organizerRoutes
import io.mbras.syt.missions.storage.MissionStorage
import io.mbras.syt.missions.storage.SignupStorage
import io.mbras.syt.missions.storage.OrganizerStorage
import io.mbras.syt.missions.auth.SessionManager
import java.io.File

fun main() {
    val dataDir = File(System.getenv("DATA_DIR") ?: "./server/data")
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val dataDir = File(System.getenv("DATA_DIR") ?: "./server/data")
    
    val missionStorage = MissionStorage(dataDir)
    val signupStorage = SignupStorage(dataDir)
    val organizerStorage = OrganizerStorage(dataDir)
    val sessionManager = SessionManager(organizerStorage)

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondText("Civic Missions API v1")
        }
        
        missionsRoutes(missionStorage, signupStorage)
        authRoutes(sessionManager, organizerStorage)
        organizerRoutes(missionStorage, signupStorage, organizerStorage, sessionManager)
    }
}
