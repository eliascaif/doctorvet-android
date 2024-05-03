package com.xionce.doctorvetServices.data;

import java.util.ArrayList;

public class Dashboard {
    private ArrayList<Waiting_room> in_waiting_rooms;
    private ArrayList<Agenda> in_agenda;
    private ArrayList<Pet> last_movements;

    public ArrayList<Waiting_room> getIn_waiting_rooms() {
        return in_waiting_rooms;
    }
    public void setIn_waiting_rooms(ArrayList<Waiting_room> in_waiting_rooms) {
        this.in_waiting_rooms = in_waiting_rooms;
    }
    public ArrayList<Agenda> getIn_agenda() {
        return in_agenda;
    }
    public void setIn_agenda(ArrayList<Agenda> in_agenda) {
        this.in_agenda = in_agenda;
    }
    public ArrayList<Pet> getLast_movements() {
        return last_movements;
    }
    public void setLast_movements(ArrayList<Pet> last_movements) {
        this.last_movements = last_movements;
    }

}
