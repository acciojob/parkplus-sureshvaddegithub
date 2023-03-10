package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
//
        try {
//        if(!parkingLotRepository3.findById(parkingLotId).isPresent()) {
//            throw new Exception("Cannot make reservation");
//        }
            ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();

//        if(!userRepository3.findById(userId).isPresent()) {
//            throw new Exception("Cannot make reservation");
//        }
            User user = userRepository3.findById(userId).get();
            if(user==null || parkingLot==null ){
                throw new Exception("Cannot make reservation");
            }
            List<Spot> spotList = parkingLot.getSpotList();


            int amt = Integer.MAX_VALUE;
            Spot spot1 = null;

            for (Spot spot : spotList) {
                if (spot.getOccupied() == false && numberOfWheels > 4 && spot.getSpotType() == SpotType.OTHERS) {
                    if (amt >= spot.getPricePerHour()) {
                        amt = spot.getPricePerHour();
                        spot1 = spot;
                    }
                } else if (spot.getOccupied() == false && (numberOfWheels > 2 && numberOfWheels <= 4) && spot.getSpotType() == SpotType.FOUR_WHEELER) {
                    if (amt >= spot.getPricePerHour()) {
                        amt = spot.getPricePerHour();
                        spot1 = spot;
                    }

                } else {
                    if (spot.getOccupied() == false && amt >= spot.getPricePerHour()) {
                        amt = spot.getPricePerHour();
                        spot1 = spot;
                    }
                }
            }

            if (spot1 == null) {
                throw new Exception("Cannot make reservation");
            }




            Reservation reservation = new Reservation();
            reservation.setNumberOfHours(timeInHours);
            reservation.setSpot(spot1);
            reservation.setUser(user);


            List<Reservation> reservationList = user.getReservationList();
            reservationList.add(reservation);
            user.setReservationList(reservationList);

            List<Reservation> reservations = spot1.getReservationList();
            reservations.add(reservation);
            spot1.setReservationList(reservations);
            spot1.setOccupied(true);


            userRepository3.save(user);
            spotRepository3.save(spot1);
            return reservation;
        }catch (Exception e){
            return null;
        }
    }
}
