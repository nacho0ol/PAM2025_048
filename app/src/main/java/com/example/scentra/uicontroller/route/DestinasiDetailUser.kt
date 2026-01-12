package com.example.scentra.uicontroller.route

object DestinasiDetailUser : DestinasiNavigasi {
    override val route = "detail_user"
    const val idUser = "idUser"
    val routeWithArgs = "$route/{$idUser}"
}