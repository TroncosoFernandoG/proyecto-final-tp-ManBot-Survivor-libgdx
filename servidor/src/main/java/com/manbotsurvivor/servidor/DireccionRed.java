package com.manbotsurvivor.servidor;

import java.net.InetAddress;

public class DireccionRed {
	
	InetAddress ip;
    int puerto;

    public DireccionRed(InetAddress ip, int puerto) {

        this.ip = ip;
        this.puerto = puerto;
    }
    
}
