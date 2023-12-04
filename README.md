<h4 align="center"><img src="assets/portable oslo readme header.png" align=center' alt="Banner" /></h4>
<br/> <br/>
<p align="center">
    <img src="https://img.shields.io/github/languages/count/panderior/portable-osclo" alt="langs" />
    <img src="https://img.shields.io/github/languages/top/panderior/portable-osclo" alt="top_lang" />
    <img src="https://img.shields.io/github/last-commit/panderior/portable-osclo" alt="last_commit" />
</p>

---
This is the Andriod and Arduino source code for the Portable Oscilloscope project.
The goal of the project is to implement hardware to measure circuit voltages using Arduino and 
transmit the reading through wifi to an Andriod app which displays the voltage reading visually.

## Project scope and limitation
The deliverable of this project extends to producing a fully functional oscilloscope that can perform the following operations.

* Display the voltage-time graph of a circuit.
* Measure and display Voltage.
* Provide features like saving graph or measurement made during experiment.
* Allow multiple smart devices to connect to the oscilloscope and get access to it.

Limitations of the portable oscilloscope to be built:

* The device is expected to operate between the frequency ranges of 1 - 10 MHz.
* The amount of voltage that can be read is limited(by far 100V with the input voltage regulator circuit implemented). 
    But the current demo does not have the voltage regulator implemented, thus the voltage support is upto 5V only
    (which is how much and Arduino Mega board can support).


## Structure
The project has two main parts. The Arduino and Andriod developments. These are placed in their
respective folders.

## Circuit design
The following is the circuit design of the input voltage regualtor at the front, Arduino at the middle, and then
ESP wifi module and LCD display at the end. The circut design is implmented using Proteus software, where the 
design and simulation files are made available in the respective directory.

<h4 align="center"><img src="assets/Final circuit.png" align=center' alt="Circuit" /></h4>

## Andriod App Design
<h4 align="center"><img src="assets/andriod app1.png" align=center' alt="Circuit" /></h4>
