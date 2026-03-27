<div align="center">
  <h1>Howl <sub><small> The Human-Woof Language Traslator</small></sub></h1>

  <p>
    <img src="https://img.shields.io/badge/Java-UTF--8%20CLI-blue" alt="Java">
    <img src="https://img.shields.io/badge/Version-1.0.0-green" alt="Version">
    <img src="https://img.shields.io/badge/Status-Available-success" alt="Status">
    <img src="https://img.shields.io/badge/License-AGPL--3.0-orange" alt="License">
    <img src="https://img.shields.io/badge/Mode-Chinese%20%7C%20English%20%7C%20Espa%C3%B1ol-purple" alt="Mode">
  </p>
</div>

<br>

## 🎉 Introduction

**Howl is a text encoder and decoder built on UTF-8 binary mapping.**

It is not a simple character replacement tool. It first turns text into a stable byte stream, then maps the data into two readable token systems:

- Chinese-style tokens: `嗷` / `呜` / `啊` / `~`
- English-style tokens: `ho` / `wl` / `au` / `~`

Howl does more than basic encode and decode. It also handles sentence-ending marks, sentence order tracking, automatic format detection, and structure recovery after decoding. The goal is not only to hide text, but also to keep the reading structure as stable as possible.

### 🚀 Quick Links
<p>
  <a href="#-core-features">✨ Core Features</a> &nbsp;|&nbsp;
  <a href="#-technical-core">🧠 Technical Core</a> &nbsp;|&nbsp;
  <a href="#-quick-start">⚙️ Quick Start</a> &nbsp;|&nbsp;
  <a href="#-encoding-map">🔣 Encoding Map</a> &nbsp;|&nbsp;
  <a href="#-license">📄 License</a>
</p>

---

## ✨ Core Features

- **Two encoding styles**: supports both Chinese-style and English-style output, and both can be decoded back.
- **Automatic input detection**: normal text is encoded, while Howl-style text is detected and decoded.
- **Sentence-level structure recovery**: the program handles bytes, ending marks, line breaks, and sentence order.
- **Multi-language CLI**: built-in prompts for Chinese, English, and Spanish.
- **Length limit and text report**: long input is checked before processing, with a simple analysis report.
- **Typewriter-like output**: terminal feedback is shown with a timed display effect.

---

## 🧠 Technical Core

> This README only shows the main design ideas. It does not expose every implementation detail.

- **The reversible mapping works on UTF-8 bytes, not on a simple character table.**
  Text is turned into binary first, then every 2 bits are mapped to one token. Because of this, Howl can handle Chinese, English, punctuation, and mixed text in one path.

- **Howl keeps text structure, not only text content.**
  Before encoding, it detects sentence-ending marks and inserts sentence number markers. During decoding, it uses these markers to rebuild the reading order.

- **Ending mark detection is not limited to one symbol.**
  It supports normal periods, question marks, exclamation marks, ellipsis forms, combined marks, and repeated ending marks in both Chinese and English styles.

- **Format detection is automatic.**
  The program checks the token pattern of one line or many lines, then chooses the correct decode path by itself.

- **The recovery path also cleans the output text.**
  After decoding, Howl removes markers, fixes extra spaces, restores order, and adjusts the first letter when needed, so the result reads more like natural text.

This is the main strength of Howl: **it joins readable encoded form and text structure recovery in one full process.**

---

## ⚙️ Quick Start

### 1. Compile

```bash
javac Howl.java
```

### 2. Run

```bash
java Howl
```

### 3. Use It in the Terminal

When the program starts, it asks you to choose an interface language:

```text
1. 中文
2. English
3. Español
```

Then you can input:

- normal text: the program encodes it
- Howl-style text: the program detects it and decodes it

> [!NOTE]
> As the program suggests, math formulas or special parts are better placed inside double quotes.

---

## 🔣 Encoding Map

### Chinese Mode

| Binary | Token |
| :--- | :--- |
| `00` | `嗷` |
| `01` | `呜` |
| `10` | `啊` |
| `11` | `~` |

### English Mode

| Binary | Token |
| :--- | :--- |
| `00` | `ho` |
| `01` | `wl` |
| `10` | `au` |
| `11` | `~` |

---

## 🧪 Use Cases

- style-based text encoding experiments
- reversible text masking and display
- interactive terminal encoding tools
- sentence-level structure recovery study
- custom token language prototypes

---

## 📌 Notes

> [!IMPORTANT]
> Howl is now a single-file Java CLI program. It is best to compile and run it in a UTF-8 environment.

- Very long input will hit the length limit.
- Decoding depends on format detection, so mixed non-Howl tokens may break recovery.
- The project focuses on structure and style, not on security encryption.

---

## 📄 License

This project is released under the [AGPL-3.0](LICENSE) license.
