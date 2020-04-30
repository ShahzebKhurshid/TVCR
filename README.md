# The Virtual Card Reader (TVCR)

### Created by Boxin Cao, Katherine Chin, & Shahzeb Khurshid
### Team 1: CS680 Spring 2020

## Overview
Utilizing smartphones’ NFC readers, **The Virtual Card Reader (TVCR)** collects data from identification cards embedded with NFC tags, and the data is populated into a contacts list. Our goal is to provide a simple, efficient way for professionals to exchange contact information. Examples of embedded identification cards are business cards and student identification cards.

## Flow

![Flow Chart](https://github.com/katherinechin/TVCR/blob/assets/flow.png)

### Flow Chart

The app has two starting points. For Start Point 1, while the app is open, tapping the NFC-enabled device on an NFC identification card will automatically bring the user to the Add page and populate the NFC tag’s data. A new Contact page can then be saved. Alternatively, for Start Point 2, opening the application on a smartphone will display the home page.

![App Overview](https://github.com/katherinechin/TVCR/blob/assets/overview.png)

### Home Page

The Home activity page lists contacts previously scanned from NFC identification cards. Additionally, the Home page has an Action Bar. The menu has a search bar item for filtering contacts in the list, and the it also has an add item that directs to the Add activity page. SQLite is used to store each contact's information.

### Contact Page

Selecting a contact from the Home list will direct to the Contact activity page that displays "Name," “Phone,” “Email," “Address,” and "LinkedIn" clickable widgets. Each will use an implicit intent to call an activity that performs dial, text, navigate, mail, or browse functions. When the “Number” widget is tapped, an alert will display options to call or message. Moreover, the menu has the option to delete this contact and return the updated Home contact list.

### Add Page

The Add page will allow the user to add new contact information, such as "Name," “Phone,” “Email,” “Address,” and "LinkedIn." Both this page and the Contact page will have a back button in the menu that brings the user back to the Home page.

## Activities
**Home** - displays ListView of saved contacts
<br/>**Add** - displays EditText fields for typing new contact information and saves contact
<br/>**Contact** - displays individual contact information
<br/>**Dialer** - call contact’s number
<br/>**SMS Messaging** - create new text message with contact’s number in recipient field
<br/>**Email** - create new email with contact’s email address in recipient field
<br/>**Google Maps** - navigation to contact’s address
<br/>**Browser** - uses contact’s URL to display LinkedIn profile using a browser

## NFC Technology
Near Field Communication (NFC) is a wireless technology that allows smartphones to share data bidirectionally with other NFC-enabled devices in close proximity of a distance less than 4 cm. Our app will populate and save contact information from an NFC tag. The connection is free and does not rely on Wi-Fi, 3G, or LTE.

### Examples of NFC Technology
[Samsung Pay/Apple Pay/Google Pay](https://www.cnet.com/news/apple-pay-google-pay-samsung-pay-best-mobile-payment-system-compared-nfc/)
<br/>[Sony’s Smart Tags for Sony Xperia Z](https://www.youtube.com/watch?v=w54ORaa754o)
<br/>[Express Transit Suica card (Japanese metro card) on smartphone](https://support.apple.com/en-us/HT207154)
<br/>[Wireless charging using NFC on Mercedes-Benz S-class](https://www.youtube.com/watch?v=LUVIFB1-vq4)
<br/>[NFC-Capable Sony music player](https://www.youtube.com/watch?v=bSJTnv8f-Zs)
<br/>['My Number Cards'](https://appleinsider.com/articles/19/06/11/japanese-iphone-users-will-be-able-to-access-my-number-cards-via-nfc-this-fall)
