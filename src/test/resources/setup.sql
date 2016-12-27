-- --------------------------------------------------------
-- Hostitel:                     127.0.0.1
-- Verze serveru:                10.1.9-MariaDB - mariadb.org binary distribution
-- OS serveru:                   Win32
-- HeidiSQL Verze:               9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Exportování struktury databáze pro

-- Exportování struktury pro tabulka dochazka.dochazka
CREATE TABLE IF NOT EXISTS `dochazka` (
  `login_id` int(11) NOT NULL,
  `jmeno` varchar(10) DEFAULT NULL,
  `date` date NOT NULL,
  `time` TIME NOT NULL,
  `type` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`login_id`,`date`,`time`)
);

-- Exportování dat pro tabulku dochazka.dochazka: ~1 rows (přibližně)
/*!40000 ALTER TABLE `dochazka` DISABLE KEYS */;
INSERT INTO `dochazka` (`login_id`, `jmeno`, `date`, `time`, `type`) VALUES
  (1, 'admin', '2016-11-24', '12:03:28.000000', 'IN'),
  (1, 'admin', '2016-11-24', '12:03:32.000000', 'OUT'),
	(1, 'admin', '2016-12-24', '12:03:28.000000', 'IN'),
	(1, 'admin', '2016-12-24', '12:03:32.000000', 'OUT'),
	(1, 'admin', '2016-12-24', '12:03:46.000000', 'IN'),
	(1, 'admin', '2016-12-24', '12:03:52.000000', 'OUT'),
	(1, 'admin', '2016-12-24', '12:03:53.000000', 'IN'),
	(1, 'admin', '2016-12-24', '12:03:54.000000', 'OUT');
/*!40000 ALTER TABLE `dochazka` ENABLE KEYS */;

-- Exportování struktury pro tabulka dochazka.svatky
CREATE TABLE IF NOT EXISTS `svatky` (
  `datum` date NOT NULL,
  PRIMARY KEY (`datum`)
);

-- Exportování dat pro tabulku dochazka.svatky: ~0 rows (přibližně)
/*!40000 ALTER TABLE `svatky` DISABLE KEYS */;
INSERT INTO `svatky` (`datum`) VALUES
	('2016-01-01');
/*!40000 ALTER TABLE `svatky` ENABLE KEYS */;

-- Exportování struktury pro tabulka dochazka.zamestnanci
CREATE TABLE IF NOT EXISTS `zamestnanci` (
  `login_id` int(11) NOT NULL,
  `jmeno` varchar(10) DEFAULT NULL,
  `heslo` varchar(10) DEFAULT NULL,
  `prava` int(11) DEFAULT NULL,
  `uvazek` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`login_id`)
);

-- Exportování dat pro tabulku dochazka.zamestnanci: ~1 rows (přibližně)
/*!40000 ALTER TABLE `zamestnanci` DISABLE KEYS */;
INSERT INTO `zamestnanci` (`login_id`, `jmeno`, `heslo`, `prava`, `uvazek`) VALUES
	(1, 'admin', '', 1, '7.5'),
	(1702, 'Petr Beneš', '', 1, '7.5');
/*!40000 ALTER TABLE `zamestnanci` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
