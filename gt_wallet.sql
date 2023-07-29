-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 16, 2023 at 12:18 PM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gt_wallet`
--

-- --------------------------------------------------------

--
-- Table structure for table `historytransaction`
--

CREATE TABLE `historytransaction` (
  `transh_ID` int(11) NOT NULL,
  `trans_ID` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `Date` varchar(155) NOT NULL,
  `trans_types` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `historytransaction`
--

INSERT INTO `historytransaction` (`transh_ID`, `trans_ID`, `uid`, `Date`, `trans_types`) VALUES
(47, 69, 1, '03/07/2023', 'CashOut'),
(48, 70, 1, '06/07/2023', 'CashOut'),
(49, 71, 1, '07/07/2023', 'CashOut'),
(50, 72, 1, '07/07/2023', 'Cash In'),
(56, 78, 1, '07/07/2023', 'Approved Request'),
(58, 80, 10, '12/07/2023 9:28:pm', 'CashOut'),
(59, 81, 1, '13/07/2023 03:00:pm', 'Cash In'),
(60, 82, 1, '13/07/2023 08:20:pm', 'Send Money'),
(61, 83, 1, '13/07/2023 08:30:pm', 'Loan'),
(62, 84, 1, '14/07/2023 02:12:pm', 'Send Money'),
(63, 85, 1, '14/07/2023 02:12:pm', 'Send Money'),
(64, 86, 10, '14/07/2023 02:15:pm', 'Send Money'),
(65, 87, 10, '14/07/2023 02:15:pm', 'Send Money'),
(66, 88, 1, '14/07/2023 02:18:pm', 'Send Money'),
(67, 89, 1, '14/07/2023 02:18:pm', 'Send Money'),
(68, 90, 1, '14/07/2023 02:29:pm', 'Send Money'),
(69, 91, 1, '14/07/2023 02:29:pm', 'Send Money'),
(70, 92, 1, '14/07/2023 02:34:pm', 'Send Money'),
(71, 93, 10, '14/07/2023 02:40:pm', 'Send Money'),
(72, 94, 10, '14/07/2023 02:40:pm', 'Send Money'),
(73, 95, 1, '14/07/2023 02:50:pm', 'Send Money'),
(74, 96, 1, '14/07/2023 02:50:pm', 'Send Money'),
(75, 97, 10, '14/07/2023 03:04:pm', 'Send Money'),
(76, 98, 10, '14/07/2023 03:04:pm', 'Send Money'),
(77, 99, 1, '14/07/2023 03:06:pm', 'Send Money'),
(78, 100, 10, '14/07/2023 03:07:pm', 'Send Money'),
(80, 102, 10, '14/07/2023 04:23:pm', 'Loan'),
(81, 103, 1, '14/07/2023 07:59:pm', 'CashOut'),
(82, 104, 1, '14/07/2023 10:46:pm', 'Loan'),
(89, 111, 1, '15/07/2023 10:29:am', 'Loan'),
(90, 112, 10, '15/07/2023 10:46:am', 'Loan'),
(91, 113, 10, '15/07/2023 11:00:am', 'Loan'),
(92, 114, 1, '15/07/2023 11:28:am', 'Loan'),
(93, 115, 10, '15/07/2023 11:11:pm', 'CashOut'),
(98, 120, 1, '16/07/2023 06:00:pm', 'Loan');

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `trans_ID` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `Trans_Date` varchar(155) NOT NULL,
  `Trans_Total` varchar(11) NOT NULL,
  `Trans_Status` varchar(155) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`trans_ID`, `uid`, `Trans_Date`, `Trans_Total`, `Trans_Status`) VALUES
(69, 1, '03/07/23', '50.19', 'CashOut'),
(70, 1, '06/07/23', '50', 'CashOut'),
(71, 1, '07/07/23', '120', 'CashOut'),
(72, 1, '07/07/23', '120', 'Cash In'),
(78, 1, '07/07/23', '120', 'Approved'),
(79, 10, '12/07/23', '200', 'Approved'),
(80, 10, '12/07/23 09:28:pm', '180', 'CashOut'),
(81, 1, '13/07/2023 03:00:pm', '500', 'Cash In'),
(82, 1, '13/07/2023 08:20:pm', '200', 'Send Money'),
(83, 1, '13/07/2023 08:30:pm', '120', 'Approved'),
(84, 1, '14/07/2023 02:12:pm', '120', 'Send Money'),
(85, 1, '14/07/2023 02:12:pm', '120', 'Send Money'),
(86, 10, '14/07/2023 02:15:pm', '200', 'Send Money'),
(87, 10, '14/07/2023 02:15:pm', '200', 'Send Money'),
(88, 1, '14/07/2023 02:18:pm', '160', 'Send Money'),
(89, 1, '14/07/2023 02:18:pm', '160', 'Send Money'),
(90, 1, '14/07/2023 02:29:pm', '160', 'Send Money'),
(91, 1, '14/07/2023 02:29:pm', '160', 'Send Money'),
(92, 1, '14/07/2023 02:34:pm', '160', 'Send Money'),
(93, 10, '14/07/2023 02:40:pm', '200', 'Send Money'),
(94, 10, '14/07/2023 02:40:pm', '200', 'Send Money'),
(95, 1, '14/07/2023 02:50:pm', '160', 'Send Money'),
(96, 1, '14/07/2023 02:50:pm', '160', 'Send Money'),
(97, 10, '14/07/2023 03:04:pm', '500', 'Send Money'),
(98, 10, '14/07/2023 03:04:pm', '500', 'Send Money'),
(99, 1, '14/07/2023 03:06:pm', '500', 'Send Money'),
(100, 10, '14/07/2023 03:07:pm', '200', 'Send Money'),
(101, 10, '14/07/2023 04:20:pm', '400', 'Approved'),
(102, 10, '14/07/2023 04:23:pm', '400', 'Approved'),
(103, 1, '14/07/2023 07:59:pm', '1000', 'CashOut'),
(104, 1, '14/07/2023 10:46:pm', '200', 'Approved'),
(105, 1, '14/07/2023 11:02:pm', '300', 'Approved'),
(106, 1, '14/07/2023 11:12:pm', '140', 'CashOut'),
(111, 1, '15/07/2023 10:29:am', '160', 'Approved'),
(112, 10, '15/07/2023 10:46:am', '200', 'Approved'),
(113, 10, '15/07/2023 11:00:am', '200', 'Approved'),
(114, 1, '15/07/2023 11:28:am', '200', 'Approved'),
(115, 10, '15/07/2023 11:11:pm', '1500', 'CashOut'),
(117, 10, '16/07/2023 05:40:pm', '300', 'Send Money'),
(119, 10, '16/07/2023 06:00:pm', '300', 'Send Money'),
(120, 1, '16/07/2023 06:00:pm', '300', 'Approved');

-- --------------------------------------------------------

--
-- Table structure for table `userrequesttbl`
--

CREATE TABLE `userrequesttbl` (
  `reqID` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `BalanceReq` varchar(100) DEFAULT NULL,
  `date` varchar(155) NOT NULL,
  `StatusReq` varchar(155) NOT NULL,
  `nameReqTo` varchar(255) NOT NULL,
  `numberReqTo` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `userrequesttbl`
--

INSERT INTO `userrequesttbl` (`reqID`, `uid`, `BalanceReq`, `date`, `StatusReq`, `nameReqTo`, `numberReqTo`) VALUES
(29, 1, '150', '03/07/23', 'Accepted', 'Hello', '09123456789'),
(30, 10, '120', '07/07/23', 'Pending', 'hehe', '09423456789'),
(31, 1, '200', '12/07/23', 'Accepted', 'hello', '09123456789'),
(32, 10, '300', '14/07/2023 03:11:pm', 'Accepted', 'Julius', '09423456789'),
(33, 1, '400', '14/07/2023 03:13:pm', 'Accepted', 'Jm', '09123456789'),
(34, 13, '160', '14/07/2023 09:01:pm', 'Accepted', 'epit', '09423456789'),
(35, 10, '200', '14/07/2023 10:45:pm', 'Declined', 'jm', '09123456789'),
(36, 10, '200', '14/07/2023 10:46:pm', 'Accepted', 'm', '09423456789'),
(37, 1, '200', '14/07/2023 10:47:pm', 'Declined', 'Jm', '09123456789'),
(38, 13, '200', '15/07/2023 11:28:am', 'Accepted', 'juls', '09423456789'),
(39, 13, '400', '15/07/2023 11:37:am', 'Pending', 'Zien', '09123456789');

-- --------------------------------------------------------

--
-- Table structure for table `usertbl`
--

CREATE TABLE `usertbl` (
  `uid` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `number` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(10) NOT NULL,
  `birthday` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `position` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `usertbl`
--

INSERT INTO `usertbl` (`uid`, `username`, `password`, `name`, `number`, `email`, `gender`, `birthday`, `address`, `position`) VALUES
(1, 'secret', '123', 'juls', '09423456789', 'popop@gmail.com', 'Male', '02/07/2000', 'Tondo Manila', 'user'),
(10, 'sec', 'ewq', '', '09123456789', '', '', '0', '', 'user'),
(12, 'jjj', '123', '123', '09123456778', 'kekeke@gmail.com', 'male', '2', 'Quezon City', 'loader'),
(13, 'uuu', '123', '123', '09123456777', 'kekeke@gmail.com', 'male', '2', 'Quezon City', 'user');

-- --------------------------------------------------------

--
-- Table structure for table `userwallettbl`
--

CREATE TABLE `userwallettbl` (
  `walletID` int(11) NOT NULL,
  `uid` int(11) NOT NULL,
  `balance` varchar(11) NOT NULL,
  `date` date NOT NULL,
  `status` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `userwallettbl`
--

INSERT INTO `userwallettbl` (`walletID`, `uid`, `balance`, `date`, `status`) VALUES
(2, 1, '540', '2023-06-20', 'Active'),
(3, 10, '50000', '2023-06-20', 'Active'),
(4, 13, '380', '2030-06-23', 'Active');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `historytransaction`
--
ALTER TABLE `historytransaction`
  ADD PRIMARY KEY (`transh_ID`),
  ADD KEY `trans_ID` (`trans_ID`),
  ADD KEY `uid` (`uid`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`trans_ID`),
  ADD KEY `uid` (`uid`);

--
-- Indexes for table `userrequesttbl`
--
ALTER TABLE `userrequesttbl`
  ADD PRIMARY KEY (`reqID`),
  ADD KEY `uid` (`uid`);

--
-- Indexes for table `usertbl`
--
ALTER TABLE `usertbl`
  ADD PRIMARY KEY (`uid`);

--
-- Indexes for table `userwallettbl`
--
ALTER TABLE `userwallettbl`
  ADD PRIMARY KEY (`walletID`),
  ADD KEY `uid` (`uid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `historytransaction`
--
ALTER TABLE `historytransaction`
  MODIFY `transh_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=99;

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `trans_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=121;

--
-- AUTO_INCREMENT for table `userrequesttbl`
--
ALTER TABLE `userrequesttbl`
  MODIFY `reqID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT for table `usertbl`
--
ALTER TABLE `usertbl`
  MODIFY `uid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `userwallettbl`
--
ALTER TABLE `userwallettbl`
  MODIFY `walletID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `historytransaction`
--
ALTER TABLE `historytransaction`
  ADD CONSTRAINT `historytransaction_ibfk_1` FOREIGN KEY (`trans_ID`) REFERENCES `transaction` (`trans_ID`),
  ADD CONSTRAINT `historytransaction_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `usertbl` (`uid`);

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `usertbl` (`uid`);

--
-- Constraints for table `userrequesttbl`
--
ALTER TABLE `userrequesttbl`
  ADD CONSTRAINT `userrequesttbl_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `usertbl` (`uid`);

--
-- Constraints for table `userwallettbl`
--
ALTER TABLE `userwallettbl`
  ADD CONSTRAINT `userwallettbl_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `usertbl` (`uid`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
