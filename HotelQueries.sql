-- 1. View hotels, their managers, and manager contact info

SELECT H.HotelName, CONCAT(S.FirstName, ' ', S.LastName) AS "Manager Name", S.PhoneNumber, S.Email
FROM HOTEL H, STAFF S
WHERE H.HotelManagerID = S.StaffID;


-- 2. View all rooms in a hotel

SELECT *
FROM ROOM
WHERE HotelID = 1;


-- 3. View rooms in a hotel by room type

SELECT *
FROM ROOM
WHERE HotelID = 5 AND RoomType = "Single-Twin";


-- 4. View rooms in a hotel by reservation status --> Current occupancy status by room in Hotel 1

SELECT RM.RoomID, RM.RoomNumber, RM.HotelID, HT.HotelName, CASE
	WHEN SYSDATE() BETWEEN RV.CheckIn AND RV.CheckOut
		THEN 'Occupied'
		ELSE 'Vacant'
		END AS "Reservation Status"
FROM ROOM RM
	LEFT JOIN ROOM_RESERVATION RR ON RM.RoomID = RR.RoomID
	LEFT JOIN RESERVATION RV ON RR.ReservationID = RV.ReservationID
	LEFT JOIN HOTEL HT ON HT.HotelID = RM.HotelID
WHERE RM.HotelID = 1;


-- 5. View current reservations and reservation information by hotel -->  Current (active) reservations at hotel 2

SELECT RM.RoomID, RM.RoomNumber, RM.HotelID, HT.HotelName, RV.*
FROM ROOM RM
	LEFT JOIN ROOM_RESERVATION RR ON RM.RoomID = RR.RoomID
	LEFT JOIN RESERVATION RV ON RR.ReservationID = RV.ReservationID
	LEFT JOIN HOTEL HT ON HT.HotelID = RM.HotelID
WHERE RM.HotelID = 2 AND SYSDATE() BETWEEN RV.CheckIn AND RV.CheckOut;


-- 6. View current reservations and reservation information by room --> All active reservations

SELECT RM.RoomID, RM.RoomNumber, RM.HotelID, HT.HotelName, RV.*
FROM ROOM RM
	LEFT JOIN ROOM_RESERVATION RR ON RM.RoomID = RR.RoomID
	LEFT JOIN RESERVATION RV ON RR.ReservationID = RV.ReservationID
	LEFT JOIN HOTEL HT ON HT.HotelID = RM.HotelID
WHERE SYSDATE() BETWEEN RV.CheckIn AND RV.CheckOut;


-- 7. View current reservations and reservation information by customer --> All active reservations for customer 11

SELECT RM.RoomID, RM.RoomNumber, RM.HotelID, HT.HotelName, RV.*
FROM ROOM RM
	LEFT JOIN ROOM_RESERVATION RR ON RM.RoomID = RR.RoomID
	LEFT JOIN RESERVATION RV ON RR.ReservationID = RV.ReservationID
	LEFT JOIN HOTEL HT ON HT.HotelID = RM.HotelID
WHERE RV.CustomerID = 11 AND SYSDATE() BETWEEN RV.CheckIn AND RV.CheckOut;


-- 8. View who is working at a hotel (shifts) and what position (staff) the employee is filling --> Who supervised the 4th of July fireworks show this year at Hotel 1?

SELECT HT.HotelID, SH.ShiftID, S.StaffID, SH.ShiftPosition, SH.ShiftStart, SH.ShiftEnd, CASE
	WHEN '2020-07-04 21:00:00' BETWEEN SH.ShiftStart AND SH.ShiftEnd
		THEN 'Scheduled'
		ELSE 'OFF'
		END AS "Shift Status"
FROM SHIFT SH
	LEFT JOIN HOTEL HT ON HT.HotelID = SH.HotelID
	LEFT JOIN STAFF S ON S.StaffID = SH.StaffID
WHERE HT.HotelID = 1;


-- 9. Search hotels by city (hotel) and available room (room reservation with date) and at what price --> Unoccupied rooms in Charleston with Price info

SELECT RM.RoomID, RM.RoomNumber, HT.City, HT.HotelName, RM.RoomPrice
FROM ROOM RM
	LEFT JOIN ROOM_RESERVATION RR ON RM.RoomID = RR.RoomID
	LEFT JOIN RESERVATION RV ON RR.ReservationID = RV.ReservationID
	LEFT JOIN HOTEL HT ON HT.HotelID = RM.HotelID
WHERE HT.City = 'Charleston' AND SYSDATE() NOT BETWEEN RV.CheckIn AND RV.CheckOut;

