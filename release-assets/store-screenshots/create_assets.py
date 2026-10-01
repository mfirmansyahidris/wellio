from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont


ROOT = Path(__file__).resolve().parent
RAW = ROOT / "raw"
PRESENTATION = ROOT / "presentation"
PRESENTATION.mkdir(parents=True, exist_ok=True)

CREAM = "#F7F7F0"
FOREST = "#245A49"
INK = "#203A31"
MUTED = "#687D73"
LIME = "#E8F0C7"
PEACH = "#F9E0CE"
WHITE = "#FFFFFF"
FONT_PATH = "/System/Library/Fonts/HelveticaNeue.ttc"


def font(size: int, index: int = 0) -> ImageFont.FreeTypeFont:
    return ImageFont.truetype(FONT_PATH, size=size, index=index)


def draw_multiline(draw: ImageDraw.ImageDraw, xy: tuple[int, int], text: str,
                   face: ImageFont.FreeTypeFont, fill: str, spacing: int) -> int:
    draw.multiline_text(xy, text, font=face, fill=fill, spacing=spacing)
    box = draw.multiline_textbbox(xy, text, font=face, spacing=spacing)
    return box[3]


def rounded_screen(path: Path, size: tuple[int, int], radius: int) -> Image.Image:
    source = Image.open(path).convert("RGB").resize(size, Image.Resampling.LANCZOS)
    mask = Image.new("L", size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, size[0] - 1, size[1] - 1),
                                           radius=radius, fill=255)
    source.putalpha(mask)
    return source


def make_poster(name: str, title: str, subtitle: str) -> Image.Image:
    canvas = Image.new("RGB", (1080, 1920), CREAM)
    draw = ImageDraw.Draw(canvas)

    draw.rounded_rectangle((850, -110, 1190, 230), radius=170, fill=LIME)
    draw.ellipse((940, 195, 1070, 325), fill=PEACH)
    draw.text((72, 64), "WELLIO  /  YOUR DAILY COMPANION", font=font(26), fill=FOREST)
    title_bottom = draw_multiline(draw, (72, 137), title, font(69, 1), INK, spacing=4)
    draw.multiline_text((76, title_bottom + 22), subtitle, font=font(31),
                        fill=MUTED, spacing=7)

    shot_size = (720, 1280)
    shot_x, shot_y = 180, 500
    frame_box = (shot_x - 19, shot_y - 19, shot_x + shot_size[0] + 19,
                 shot_y + shot_size[1] + 19)

    shadow = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    shadow_draw = ImageDraw.Draw(shadow)
    shadow_draw.rounded_rectangle(frame_box, radius=67, fill=(20, 43, 34, 90))
    shadow = shadow.filter(ImageFilter.GaussianBlur(26))
    canvas = Image.alpha_composite(canvas.convert("RGBA"), shadow)
    frame = ImageDraw.Draw(canvas)
    frame.rounded_rectangle(frame_box, radius=67, fill="#183D31")

    screen = rounded_screen(RAW / f"{name}.png", shot_size, radius=49)
    canvas.alpha_composite(screen, (shot_x, shot_y))
    return canvas.convert("RGB")


POSTERS = [
    ("today", "Your day,\nthoughtfully planned.",
     "Movement, meals and small wins in one place."),
    ("workout", "A little movement.\nA lift for your day.",
     "Find your own rhythm. Rest whenever you need to."),
    ("meals", "Good food.\nLess overthinking.",
     "Simple ideas for breakfast, lunch, snacks and dinner."),
    ("profile", "Progress at\nyour own pace.",
     "Notice the little wins that add up over time."),
    ("recipe-detail", "Simple ingredients.\nA moment to enjoy.",
     "Explore meal ideas with practical ingredients and prep times."),
    ("workout-session", "One set\nat a time.",
     "Follow clear instructions and take each move at your pace."),
    ("meal-options", "Plans change.\nMeals can too.",
     "Replace one meal while keeping the rest of your day."),
    ("recipe-steps", "Clear steps.\nEasy everyday meals.",
     "See what you need and how to prepare it."),
]

for name, title, subtitle in POSTERS:
    make_poster(name, title, subtitle).save(PRESENTATION / f"{name}.png", optimize=True)


def make_feature_graphic() -> Image.Image:
    canvas = Image.new("RGB", (1024, 500), CREAM)
    draw = ImageDraw.Draw(canvas)
    draw.ellipse((655, -95, 1115, 365), fill=LIME)
    draw.ellipse((856, 300, 1034, 478), fill=PEACH)
    draw.text((54, 52), "WELLIO", font=font(30, 1), fill=FOREST)
    draw.multiline_text((54, 116), "Small steps.\nA healthier you.",
                        font=font(59, 1), fill=INK, spacing=1)
    draw.text((58, 285), "Workouts  ·  Meals  ·  Daily routines",
              font=font(23), fill=MUTED)
    draw.rounded_rectangle((57, 345, 303, 397), radius=26, fill=FOREST)
    draw.text((78, 356), "Made for your day", font=font(20, 1), fill=WHITE)

    source = Image.open(RAW / "today.png").convert("RGB")
    preview_size = (272, 484)
    source = source.resize(preview_size, Image.Resampling.LANCZOS)
    mask = Image.new("L", preview_size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, preview_size[0] - 1,
                                             preview_size[1] - 1),
                                            radius=30, fill=255)
    source.putalpha(mask)
    shadow = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    ImageDraw.Draw(shadow).rounded_rectangle((696, 13, 984, 497), radius=40,
                                              fill=(20, 43, 34, 100))
    shadow = shadow.filter(ImageFilter.GaussianBlur(15))
    canvas = Image.alpha_composite(canvas.convert("RGBA"), shadow)
    ImageDraw.Draw(canvas).rounded_rectangle((696, 13, 984, 497), radius=40,
                                               fill="#183D31")
    canvas.alpha_composite(source, (704, 20))
    return canvas.convert("RGB")


make_feature_graphic().save(ROOT.parent / "wellio-feature-graphic.png", optimize=True)

thumb_w, thumb_h = 270, 480
gap, margin, label_h = 24, 28, 42
sheet = Image.new("RGB", (margin * 2 + 4 * thumb_w + 3 * gap,
                           margin * 2 + 2 * (thumb_h + label_h) + gap), WHITE)
sheet_draw = ImageDraw.Draw(sheet)
for idx, (name, _, _) in enumerate(POSTERS):
    col, row = idx % 4, idx // 4
    x = margin + col * (thumb_w + gap)
    y = margin + row * (thumb_h + label_h + gap)
    image = Image.open(PRESENTATION / f"{name}.png").convert("RGB")
    image = image.resize((thumb_w, thumb_h), Image.Resampling.LANCZOS)
    sheet.paste(image, (x, y))
    sheet_draw.text((x, y + thumb_h + 8), name.replace("-", " ").title(),
                    font=font(18, 1), fill=INK)
sheet.save(ROOT.parent / "store-screenshots-preview.jpg", quality=90, optimize=True)
