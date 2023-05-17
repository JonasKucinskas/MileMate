![MileMate Logo](https://cdn.discordapp.com/attachments/1080586295987941491/1108164220870197309/ic_launcher.png)
# MileMate

University semester task for computer-science students.

## Team

•	Alanas Švažas - Product owner
•	Jonas Kučinskas - Scrum master
•	Deividas Grinius - Developer
•	Edvinas Serapinas - Developer
•	Mantas Vansauskas - Developer


## What is MileMate?

"MileMate" is a mobile app for Android devices that will use the user's car data to notify the user, via push notifications or other means, when various car components need to be checked or replaced, such as: when to change the car's tyres, when the season changes; when to check the car's oil; when the car's MOT is due. Some data will be collected with the help of GPS, but most of the data has to be collected by the user. The app also supports multi-car information.

## Achitecture

![Architecture_img](https://media.discordapp.net/attachments/1080586295987941491/1108128485681156157/Architektura.png?width=1089&height=554)

## How to use MileMate

When app is first opened user is presented with login/register screen. Once login information is entered, user can access and use our app.

In order to use MileMate to it's full potential, user can enter their car into our app by pressing "ADD CAR" button. User can also set check-up reminder for their car by pressing "Expiry date set at" text box. Then a calendar will open where user can choose a date for their check-up. To set a reminder, user has to pick desired month and day for their licence expiry date. Seasonal tire change reminders are set automatically once country of resirence is selected.

In order to start your trip and view such information as current fuel consumption, speed, cost of current trip and fuel used, user has to select desired vehicle and click "DRIVE" button.

## Testing

### 1. Registration and log-in

![Reg1_img](https://media.discordapp.net/attachments/1108332970672132249/1108333022966726696/image.png?width=161&height=346)

Test to see if multiple usernames are available.

![Reg2_img](https://media.discordapp.net/attachments/1108332970672132249/1108333023314849852/image.png?width=161&height=350)

Test to see if password matches criteria (1 uppercase and 8 symbols).

![Reg3_img](https://cdn.discordapp.com/attachments/1108332970672132249/1108333023621029888/image.png)

Test to see if registration is successful if passwords don't match.

![Login_img](https://cdn.discordapp.com/attachments/1108332970672132249/1108333023952388126/image.png)

Test to see if logging in is successful upon entering invalid data.

### 2. Car check-up

![Tech1_img](https://media.discordapp.net/attachments/1108332970672132249/1108334352309424269/image.png?width=189&height=397)

Testing selecting car check-up expiry selection.

![Tech2_img](https://media.discordapp.net/attachments/1108332970672132249/1108334352653369476/image.png?width=192&height=402)

Testing car expiry reminder date selection.

### 3. Car add

![Add1_img](https://media.discordapp.net/attachments/1108332970672132249/1108335029597261935/image.png?width=165&height=363) 
![Add2_img](https://media.discordapp.net/attachments/1108332970672132249/1108335029861486622/image.png?width=166&height=358)
![Add3_img](https://media.discordapp.net/attachments/1108332970672132249/1108335030222192680/image.png?width=174&height=355)

Testing to see if new cars can be added.

### 4. User driver's license expiry

![License1_img](https://media.discordapp.net/attachments/1108332970672132249/1108335549862920254/image.png?width=265&height=564) 
![License2_img](https://media.discordapp.net/attachments/1108332970672132249/1108335550185865216/image.png?width=375&height=350)
Test to see if reminder to renew user's driver's license works.
