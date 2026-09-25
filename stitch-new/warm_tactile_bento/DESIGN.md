---
name: Warm Tactile Bento
colors:
  surface: '#f8faf4'
  surface-dim: '#d8dbd5'
  surface-bright: '#f8faf4'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4ee'
  surface-container: '#ecefe9'
  surface-container-high: '#e7e9e3'
  surface-container-highest: '#e1e3dd'
  on-surface: '#191c19'
  on-surface-variant: '#42493c'
  inverse-surface: '#2e312e'
  inverse-on-surface: '#eff1ec'
  outline: '#72796b'
  outline-variant: '#c2c9b8'
  surface-tint: '#376a1c'
  primary: '#205203'
  on-primary: '#ffffff'
  primary-container: '#386b1d'
  on-primary-container: '#afea8c'
  inverse-primary: '#9cd67b'
  secondary: '#b61718'
  on-secondary: '#ffffff'
  secondary-container: '#da342d'
  on-secondary-container: '#fffbff'
  tertiary: '#4b445a'
  on-tertiary: '#ffffff'
  tertiary-container: '#635c73'
  on-tertiary-container: '#dfd6f1'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#b7f394'
  primary-fixed-dim: '#9cd67b'
  on-primary-fixed: '#082100'
  on-primary-fixed-variant: '#1f5103'
  secondary-fixed: '#ffdad5'
  secondary-fixed-dim: '#ffb4ab'
  on-secondary-fixed: '#410002'
  on-secondary-fixed-variant: '#930009'
  tertiary-fixed: '#e8defa'
  tertiary-fixed-dim: '#cbc2dd'
  on-tertiary-fixed: '#1e182c'
  on-tertiary-fixed-variant: '#4a4359'
  background: '#f8faf4'
  on-background: '#191c19'
  surface-variant: '#e1e3dd'
typography:
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '800'
    lineHeight: 36px
    letterSpacing: 0.04em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '800'
    lineHeight: 32px
    letterSpacing: 0.04em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '800'
    lineHeight: 28px
    letterSpacing: 0.03em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '700'
    lineHeight: 24px
    letterSpacing: 0.02em
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 28px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '700'
    lineHeight: 20px
    letterSpacing: 0.05em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 18px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.375rem
  space-sm: 0.75rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.25rem
---

## Brand & Style

This design system is engineered specifically for elder care, early-to-moderate dementia support, and multi-generational companionship. It rejects the sterile, anxiety-inducing aesthetics of traditional medical software in favor of a comforting, tactile, domestic environment reminiscent of physical board pieces, embossed stationery, and familiar everyday keepsakes.

### Core Tenets
- **Dignified & Age-Positive:** The interface never infantilizes the user. It treats elders with respect through high-craft layouts, confident typography, and purposeful physical cues rather than juvenile iconography.
- **Tremor & Motor Accommodation:** Interaction zones prioritize mechanical affordance. Every primary actionable item features an enforced physical surface height (tactile rim) that depresses flush on interaction, offering unambiguous mechanical feedback for unsteady fingers.
- **Multilingual Clarity:** Every functional anchor pairs Latin script with Devanagari and Gujarati scripts to accommodate familial linguistic nuances across multilingual Indian households.
- **Zero Ambiguity Bento Architecture:** Information is chunked into discrete, high-contrast bento tiles with dedicated categorical color identities. Content never drifts across unbounded planes or hides beneath invisible gestural layers.

## Colors

The palette relies on high-contrast chroma pairings balanced on warm, organic earth bases to maintain visual stamina and prevent cognitive fatigue.

### Core Hierarchy
- **Canvas Base (`#FBFDF7`):** Warm milk-white background that prevents glare and optical halation for aging retinas.
- **Surface Crisp (`#FFFFFF`):** High-reflectance tile base surrounded by physical borders, never using opacity or translucency.
- **Header Enclosure (`#DDE6C7`):** Calming, olive-tinted framing bar flanked with a `#C6CFB1` mechanical border.
- **Brand Primary (`#386B1D`):** Deep botanical green providing greater than 7:1 contrast on white and cream tones; container fill is `#E2F1CD` with `#FFFFFF` text on primary fills.
- **Urgent Emergency (`#BA1A1A`):** High-alert distress red. Pressed state deepens to `#93000A` with a distinct structural border of `#800006` and `#FFFFFF` text.

### Neutral Typography Tones
- **Text Main (`#1A1C18`):** Deep charcoal-olive ink for optimal contrast.
- **Text Subtitle (`#45483D`):** Mid-tone supporting shade with 4.5:1 minimum contrast.
- **Text Muted (`#74796B`):** Structural tertiary label color, reserved solely for non-critical contextual indicators.

### Bento Categorical Modules (Background / Border / Typography)
1. **People:** `#FCE1D1` / `#E5B69E` / `#5C280D` (Warm Terracotta)
2. **My Day:** `#E0E2ED` / `#BFC3D4` / `#242D42` (Slate Slate)
3. **Memories:** `#F7F2D7` / `#DBD3AA` / `#4A3F14` (Aged Linen)
4. **Play:** `#D1E8FF` / `#A5C7E6` / `#0D3A63` (Clear Sky)
5. **Medicine:** `#FFE0D6` / `#F8BBA8` / `#6E1C06` (Coral Alert)
6. **Mood:** `#D7F3DF` / `#AEE0BE` / `#144D25` (Sage Growth)
7. **Today:** `#E2F1CD` / `#C6DF9D` / `#2B5216` (Olive Sprout)
8. **Ask Saathi:** `#EDE3FF` / `#D2BEFF` / `#3B1A7A` (Gentle Lavender)

## Typography

Typography prioritizes extreme visual legibility, strong word shaping, and explicit letter separation. The base font family is **Plus Jakarta Sans**, combined alongside **Noto Sans Devanagari** and **Noto Sans Gujarati** for multivariant scripts.

### Typographic Principles
- **No Text Below 14px:** Absolute prohibition of small subtexts. The baseline patient reading size is pinned strictly to 16px (`body-md`) and 18px (`body-lg`).
- **Uppercase Display Anchors:** All section anchors and high-level card identifiers use uppercase formatting paired with extended tracking (`0.03em` to `0.05em`) to assist peripheral scanning and reading comprehension.
- **Trilingual Pairings:** Labels stack Latin, Devanagari, and Gujarati text consistently. Script scales are matched visually—Devanagari and Gujarati run at 100% of Latin size with equivalent weights (`Noto Sans` SemiBold/Bold) to maintain structural visual balance.

## Layout & Spacing

The layout is optimized for an Android viewport baseline of **360px × 800px**, operating on an explicit tactile bento grid with high spatial isolation.

### Grid & Density
- **Outer Canvas Margins:** 16px (`margin: 1rem`) on outer bounds to maximize viewport real estate while isolating interactive content away from device edge gesture collision zones.
- **Grid Gutter:** 16px (`gutter: 1rem`) uniform horizontal and vertical spacing between cards.
- **Minimum Tap Target Matrix:** Interactive elements maintain an absolute minimum touch boundary of **64px × 64px**, with 8px to 12px deadzones between adjacent tap zones to accommodate tremors and imprecise contact patterns.
- **Reflow Architecture:** On wider viewports (tablets/foldables), the single-to-two-column bento modules scale into an asymmetrical 3-column masonry grid, preserving standard component heights without disproportionate horizontal distortion.

## Elevation & Depth

This system avoids blurred ambient drop shadows, translucent materials, and skeuomorphic gradients that create muddy edges or visual confusion for impaired eyesight.

### Mechanical Elevation Style
- **Hard Border Invariant:** Every component utilizes an explicit `2px solid` border. Borders are saturated and 15–20% darker than the parent fill to define unambiguous structural perimeters.
- **Hard Tactile Shelf (Physical Offset):** Depth is created via a hard, non-blurred bottom ridge (`box-shadow: 0px 4px 0px [border-color]`).
- **Press State Physics:** When tapped or held, interactive elements shift downward by 4px (`transform: translateY(4px)`), while the hard shelf drops to `0px 0px 0px [border-color]`. This physical displacement gives real-time tactile validation of intent without relying exclusively on auditory or haptic confirmations.

## Shapes

Shapes are soft, protective, and organic, designed to minimize visual sharpness while retaining clear internal layout containers.

### Shape Geometry Rules
- **Bento Modules:** Built with an ultra-generous radius of `36px`, softening module corners to evoke smoothed wooden blocks.
- **Interactive Action Buttons:** Formed as continuous full-radius pills (`border-radius: 9999px`) to immediately convey clickability.
- **Tags & Category Chips:** Pill forms configured at fixed `44px` height with `border-radius: 9999px`.
- **Text Inputs & Steppers:** Contained within `20px` radiused geometric envelopes to distinguish data entry from action buttons.

## Components

### 1. Bento Modules (Tiles)
- **Structure:** Solid background color from the module palette, `2px solid` outline matching the categorical border token, and a `0px 4px 0px` hard tactile offset using that same border token.
- **Internal Padding:** 20px padding on all sides.
- **Header:** Icon badge (48px circle, solid surface fill), followed by the bilingual/trilingual title stack.
- **Feedback:** Depresses 4px with zero transition delay on pointer-down to acknowledge tremors immediately.

### 2. Primary & Action Buttons
- **Height & Radius:** Minimum 64px height; `border-radius: 9999px`.
- **Style:** Brand Green (`#386B1D`) or Emergency Red (`#BA1A1A`), paired with a `2px solid` deeper-tone border and matching `0px 4px 0px` bottom shelf.
- **Typography:** Bold 18px (`body-lg`), centered, high-contrast white text (`#FFFFFF`) with stacked regional translations beneath.

### 3. Chips & Category Filters
- **Dimensions:** 44px fixed height, `border-radius: 9999px`, horizontal padding of 20px.
- **States:** Inactive chips feature an off-white background (`#FFFFFF`) with a `2px solid #C6CFB1` border. Active chips feature `#E2F1CD` fill, `#386B1D` border, and `#2B5216` text.

### 4. Form Inputs & Text Fields
- **Container:** Minimum 64px height, `20px` radius, `#FFFFFF` fill, `2px solid #74796B` perimeter.
- **Active / Focused:** Border thickens to `3px solid #386B1D`, zero blur halo, high-contrast `#1A1C18` text at 18px font size.

### 5. Checkboxes & Radio Selectors
- **Scale:** Minimum size of `36px × 36px` centered inside a non-interactive `64px` tap target box.
- **Geometry:** Checkboxes use `10px` radius; radio controls use perfect circular geometries.
- **Affordance:** Unselected states feature an empty `#FFFFFF` fill with `2px solid #74796B`. Selected states feature `#386B1D` fill with a `4px` solid internal white check icon or concentric pip.

### 6. Emergency SOS Bar
- **Positioning:** Pinned to bottom viewport margin with safe-area spacing.
- **Visuals:** `#BA1A1A` fill, `#800006` border (`2px solid`), with `0px 4px 0px #800006` depth. Labelled in clear, high-contrast white with English ("EMERGENCY HELP"), Devanagari ("आपातकालीन सहायता"), and Gujarati ("કટોકટી સહાય").