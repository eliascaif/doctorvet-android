package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Pet implements DoctorVetApp.IResourceObject {
    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private Date birthday;
    @Expose
    private BigDecimal weight;
    @Expose
    private String chip;
    @Expose
    private Integer death;
    @Expose
    private String notes;
    @Expose
    private Integer photo_deleted;
    @Expose
    private Pet_race race;
    @Expose
    private Pet_pelage pelage;
    @Expose
    private Pet_gender gender;
    @Expose
    private Pet_character character;
    @Expose
    private ArrayList<Resource> resources;
    @Expose
    private ArrayList<Owner> owners;

    private Date last_visit;
    private Pet_states states_pet;
    private User user;
    private String reason;
    private String reason_es;
    private String photo_url;
    private String thumb_url;
    private String age;

    public Pet() {
    }

    public Pet(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Pet(Integer id, String name, String thumb_url) {
        this.id = id;
        this.name = name;
        this.thumb_url = thumb_url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getChip() {
        return chip;
    }

    public Integer getDeath() {
        return death;
    }

    public void setDeath(Integer death) {
        this.death = death;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public Date getLast_visit() {
        return last_visit;
    }

    public ArrayList<Owner> getOwners() {
        if (this.owners == null)
            this.owners = new ArrayList<>();

        return owners;
    }

    public void setOwners(ArrayList<Owner> owners) {
        this.owners = owners;
    }

    public Pet_states getStates_pet() {
        return states_pet;
    }

    public Pet_race getRace() {
        return race;
    }

    public Pet_pelage getPelage() {
        return pelage;
    }

    public Pet_gender getGender() {
        return gender;
    }

    public Pet_character getCharacter() {
        return character;
    }

    public void setRace(Pet_race race) {
        this.race = race;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getReason_es() {
        if (this.reason_es == null)
            return "";

        return reason_es;
    }
    public void setReason_es(String reason) {
        this.reason_es = reason;
    }
    public String getOwnersNames() {
        String owners_names = "";
        for (Owner owner : owners) {
            owners_names += owner.getName() + ", ";
        }

        //saco ultima ", "
        int last = owners_names.lastIndexOf(", ");
        if (last > 0)
            owners_names = owners_names.substring(0, owners_names.length()-2);

        return owners_names;
    }
//    public String getAge() {
//        String edad = "";
//        if (birthday != null) {
//            edad = HelperClass.calculateAge(birthday);
//        }
//        return edad;
//    }
    public Owner getFirstPrincipalOwner() {
        for (Owner owner : owners) {
            //para listas min, el propietario que viene puesto es el principal
            if (owner.getIs_principal() != null && owner.getIs_principal() == 1)
                return owner;
        }

        if (owners.size() > 0)
            return owners.get(0);

        return null;
    }
    public void setPelage(Pet_pelage pelage) {
        this.pelage = pelage;
    }
    public void setGender(Pet_gender gender) {
        this.gender = gender;
    }
    public void setCharacter(Pet_character character) {
        this.character = character;
    }
    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }

    public Pet getPolish() {
        Pet pet_polish = new Pet(getId(), getName(), getThumb_url());
        return pet_polish;
    }

    public enum Supply {PENDING, EXPIRED, PENDING_AND_EXPIRED, NA}
    public class Pet_states {
        private Boolean is_birthday = false;
        private Supply supply;
        private ArrayList<Agenda> appointments_tasks = new ArrayList<>();
        private Pet_last_visit last_visit;
//        private Integer is_in_waiting_room;
        private Waiting_room waiting_room;
        private BigDecimal food_level;
        private Pet_last_food last_food;
        private Boolean life_expectancy;

        public Pet_states() {
        }

        public BigDecimal getFood_level() {
            return food_level;
        }

        public void setFood_level(BigDecimal food_level) {
            this.food_level = food_level;
        }

        public Pet_last_food getLast_food() {
            return last_food;
        }

        public void setLast_food(Pet_last_food last_food) {
            this.last_food = last_food;
        }

        public Boolean getIs_birthday() {
            return is_birthday;
        }
        public void setIs_birthday(boolean is_birthday) {
            this.is_birthday = is_birthday;
        }

        public Boolean getLife_expectancy() {
            return life_expectancy;
        }

        public void setLife_expectancy(Boolean life_expectancy) {
            this.life_expectancy = life_expectancy;
        }

        public Supply getSuministro_planificado() {
            return supply;
        }
        public void setSuministro_planificado(Supply plannedsupply) {
            this.supply = plannedsupply;
        }
        public ArrayList<Agenda> getAppointments_tasks() {
            return appointments_tasks;
        }
        public void setAppointments_tasks(ArrayList<Agenda> appointments_tasks) {
            this.appointments_tasks = appointments_tasks;
        }
        public Pet_last_visit getLast_visit() {
            return last_visit;
        }
        public void setLast_visit(Pet_last_visit last_visit) {
            this.last_visit = last_visit;
        }

        public class Pet_last_visit {
            private Date date;
            private String reason;
            private Integer id_user;
            private String user_name;

            public Date getDate() {
                return date;
            }

            public void setDate(Date date) {
                this.date = date;
            }

            public String getReason() {
                return reason;
            }

            public String getReasonInString() {
                if (reason.equalsIgnoreCase("clinic"))
                    return "Clínica";

                if (reason.equalsIgnoreCase("supply"))
                    return "Suministro";

                if (reason.equalsIgnoreCase("sell"))
                    return "Venta";

                if (reason.equalsIgnoreCase("recipe"))
                    return "Receta";

                if (reason.equalsIgnoreCase("clinic2"))
                    return "Clínica extendida";

                return "";
            }

            public void setReason(String reason) {
                this.reason = reason;
            }

            public Integer getId_user() {
                return id_user;
            }

            public void setId_user(Integer id_user) {
                this.id_user = id_user;
            }

            public String getUser_name() {
                return user_name;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }
        }
        public class Pet_last_food {

            private Date date;
            private String product_name;

            public Pet_last_food(Date date, String product_name) {
                this.date = date;
                this.product_name = product_name;
            }

            public Date getDate() {
                return date;
            }

            public void setDate(Date date) {
                this.date = date;
            }

            public String getProduct_name() {
                return product_name;
            }

            public void setProduct_name(String product_name) {
                this.product_name = product_name;
            }
        }
        public Waiting_room getWaiting_room() {
            return waiting_room;
        }
        public void setWaiting_room(Waiting_room waiting_room) {
            this.waiting_room = waiting_room;
        }
    }

    @Override
    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    @Override
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    @Override
    public String getPhoto_url() {
        return photo_url;
    }

    @Override
    public void setThumb_deleted(Integer value) {
        this.photo_deleted = value;
    }

    @Override
    public ArrayList<Resource> getResources() {
        if (this.resources == null)
            this.resources = new ArrayList<Resource>();

        return resources;
    }

    @Override
    public String toString() {
        return getName();
    }

    public class Get_pagination_pets extends Get_pagination {
        @Expose
        private ArrayList<Pet> content;

        public ArrayList<Pet> getContent() {
            return content;
        }
        public void setContent(ArrayList<Pet> content) {
            this.content = content;
        }
    }

    public class Pets_for_input {
        //private ArrayList<Owner> owners;
        private ArrayList<Pet_race> pets_races;
        private ArrayList<Pet_pelage> pets_pelages;
        private ArrayList<Pet_gender> pets_genders;
        private ArrayList<Pet_character> pets_characters;

        public ArrayList<Owner> getOwners() {
            return owners;
        }

//        public void setOwners(ArrayList<Owner> owners) {
//            this.owners = owners;
//        }
        public ArrayList<Pet_race> getPets_races() {
            return pets_races;
        }
        public void setPets_races(ArrayList<Pet_race> pets_races) {
            this.pets_races = pets_races;
        }
        public ArrayList<Pet_pelage> getPets_pelages() {
            return pets_pelages;
        }
        public void setPets_pelages(ArrayList<Pet_pelage> pets_pelages) {
            this.pets_pelages = pets_pelages;
        }
        public ArrayList<Pet_gender> getPets_genders() {
            return pets_genders;
        }
        public void setPets_genders(ArrayList<Pet_gender> pets_genders) {
            this.pets_genders = pets_genders;
        }
        public ArrayList<Pet_character> getPets_characters() {
            return pets_characters;
        }
        public void setPets_characters(ArrayList<Pet_character> pets_characters) {
            this.pets_characters = pets_characters;
        }

    }

}



