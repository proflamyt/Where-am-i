package com.example.realgps;

import android.os.Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;




public class sendCoordinate extends Thread {
    private String message;
    private String ipAddress;
    private int port;

    public sendCoordinate(String message, String ipAddress, int port) {
        this.message = message;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void run() {

        DatagramSocket socket = null;
        try {
            // Convert the message to bytes
            byte[] sendData = message.getBytes();

            // Get the InetAddress object for the destination IP address
            InetAddress address = InetAddress.getByName(ipAddress);

            // Create a UDP socket
            socket = new DatagramSocket();

            // Create a UDP packet with the message, destination address, and port
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, port);

            // Send the packet
            socket.send(packet);

            // Close the socket
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket !=null) socket.close();
        }
    }
}
