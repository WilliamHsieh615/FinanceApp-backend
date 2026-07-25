import json
import os
import time
from decimal import Decimal
from getpass import getpass
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode
from urllib.request import Request, urlopen

# ============================================================
# FinFun Country SQL Generator
# Version : V1
# Author  : William Hsieh
# ============================================================

INPUT_FILE = "/Users/williamhsieh/Desktop/countries.json"
OUTPUT_FILE = "/Users/williamhsieh/Desktop/countries.sql"

# REST Countries v5 is used only for:
#   - languages
#   - country_languages
#
# Set the key before running:
#   export RESTCOUNTRIES_API_KEY="rc_live_..."
RESTCOUNTRIES_API_KEY = (
    os.getenv("RESTCOUNTRIES_API_KEY")
    or getpass("REST Countries API Key: ").strip()
)
RESTCOUNTRIES_API_URL = "https://api.restcountries.com/countries/v5"
RESTCOUNTRIES_PAGE_SIZE = 100
RESTCOUNTRIES_TIMEOUT = 30
RESTCOUNTRIES_MAX_RETRIES = 3

# ============================================================
# Resource Code
# ============================================================

RESOURCE_PROVIDER = "FLAGCDN"
STORAGE_PROVIDER = "EXTERNAL"

ENTITY_TYPE = "countries"

FILE_ROLE = "FLAG"

FILE_TYPE = "PNG"

# ============================================================
# Currency Setting
# decimal_places
# is_fiat
# is_crypto
# ============================================================

CURRENCY_SETTING = {

    # 亞洲
    "TWD": (Decimal("0.01"), True, False),
    "JPY": (Decimal("1"), True, False),
    "KRW": (Decimal("1"), True, False),
    "CNY": (Decimal("0.01"), True, False),
    "HKD": (Decimal("0.01"), True, False),
    "MOP": (Decimal("0.01"), True, False),
    "SGD": (Decimal("0.01"), True, False),
    "MYR": (Decimal("0.01"), True, False),
    "THB": (Decimal("0.01"), True, False),
    "IDR": (Decimal("1"), True, False),
    "PHP": (Decimal("0.01"), True, False),
    "VND": (Decimal("1"), True, False),
    "INR": (Decimal("0.01"), True, False),

    # 歐美
    "USD": (Decimal("0.01"), True, False),
    "CAD": (Decimal("0.01"), True, False),
    "EUR": (Decimal("0.01"), True, False),
    "GBP": (Decimal("0.01"), True, False),
    "CHF": (Decimal("0.01"), True, False),
    "SEK": (Decimal("0.01"), True, False),
    "NOK": (Decimal("0.01"), True, False),
    "DKK": (Decimal("0.01"), True, False),

    # 中東
    "AED": (Decimal("0.01"), True, False),
    "SAR": (Decimal("0.01"), True, False),
    "QAR": (Decimal("0.01"), True, False),
    "OMR": (Decimal("0.001"), True, False),
    "BHD": (Decimal("0.001"), True, False),
    "KWD": (Decimal("0.001"), True, False),

    # Crypto
    "BTC": (Decimal("0.00000001"), False, True),
    "ETH": (Decimal("0.00000001"), False, True)
}

DEFAULT_DECIMAL = Decimal("0.01")

# ============================================================
# Helper
# ============================================================

def sql(value):

    if value is None:
        return "NULL"

    if isinstance(value, bool):
        return "TRUE" if value else "FALSE"

    if isinstance(value, Decimal):
        return str(value)

    value = str(value)

    if value == "":
        return "NULL"

    value = value.replace("'", "''")

    return f"'{value}'"


def get_currency_setting(code):

    return CURRENCY_SETTING.get(
        code,
        (
            DEFAULT_DECIMAL,
            True,
            False
        )
    )


def first_value(data, *keys):

    for key in keys:

        value = data.get(key)

        if value not in (None, ""):
            return value

    return None


def language_code(language):

    # Prefer ISO 639-1 so the same language has a compact, stable code.
    # Fall back to ISO 639-3 or BCP 47 when ISO 639-1 is unavailable.
    code = first_value(
        language,
        "iso_639_1",
        "iso639_1",
        "alpha_2",
        "code_2",
        "iso_639_3",
        "iso639_3",
        "alpha_3",
        "code_3",
        "bcp_47",
        "bcp47",
        "code"
    )

    if not code:
        return None

    return str(code).strip().lower()


def language_name(language):

    name = first_value(
        language,
        "name",
        "english_name",
        "english",
        "common"
    )

    if isinstance(name, dict):
        name = first_value(name, "common", "official", "english")

    return str(name).strip() if name else None


def language_boolean(language, default, *keys):

    value = first_value(language, *keys)

    if value is None:
        return default

    if isinstance(value, bool):
        return value

    return str(value).strip().lower() in {
        "1",
        "true",
        "yes"
    }


def fetch_rest_countries():

    if not RESTCOUNTRIES_API_KEY:
        raise RuntimeError(
            "Missing RESTCOUNTRIES_API_KEY. "
            "Set it in the environment before running this program."
        )

    all_countries = []
    offset = 0

    while True:

        query = urlencode({
            "response_fields": "codes.alpha_2,languages",
            "limit": RESTCOUNTRIES_PAGE_SIZE,
            "offset": offset
        })

        url = f"{RESTCOUNTRIES_API_URL}?{query}"

        for attempt in range(1, RESTCOUNTRIES_MAX_RETRIES + 1):

            request = Request(
                url,
                headers={
                    "Authorization": f"Bearer {RESTCOUNTRIES_API_KEY}",
                    "Accept": "application/json",
                    "User-Agent": "FinFun-Country-SQL-Generator/2"
                }
            )

            try:

                with urlopen(
                    request,
                    timeout=RESTCOUNTRIES_TIMEOUT
                ) as response:

                    payload = json.load(response)

                break

            except HTTPError as ex:

                body = ex.read().decode("utf-8", errors="replace")

                if ex.code == 429 and attempt < RESTCOUNTRIES_MAX_RETRIES:
                    retry_after = ex.headers.get("Retry-After", "1")
                    time.sleep(max(1, int(retry_after)))
                    continue

                raise RuntimeError(
                    f"REST Countries HTTP {ex.code}: {body}"
                ) from ex

            except (URLError, TimeoutError) as ex:

                if attempt < RESTCOUNTRIES_MAX_RETRIES:
                    time.sleep(attempt)
                    continue

                raise RuntimeError(
                    f"REST Countries request failed: {ex}"
                ) from ex

        data = payload.get("data", {})
        objects = data.get("objects", [])
        meta = data.get("meta", {})

        if not isinstance(objects, list):
            raise RuntimeError(
                "Unexpected REST Countries response: "
                "data.objects is not a list."
            )

        all_countries.extend(objects)

        if not meta.get("more"):
            break

        count = meta.get("count", len(objects))

        if not count:
            break

        offset += count

    return all_countries


# ============================================================
# Read JSON
# ============================================================

if not os.path.exists(INPUT_FILE):

    raise FileNotFoundError(INPUT_FILE)

with open(INPUT_FILE, "r", encoding="utf-8") as f:

    countries = json.load(f)

print(f"Country Count : {len(countries)}")

print("Fetch Languages From REST Countries...")

rest_countries = fetch_rest_countries()

print(f"REST Countries Count : {len(rest_countries)}")

# ============================================================
# SQL
# ============================================================

sqls = [

    "USE finance_app;",
    "SET NAMES utf8mb4;",
    "",

    "-- =========================================",
    "-- Clean Data",
    "-- =========================================",

    "SET FOREIGN_KEY_CHECKS = 0;",

    "TRUNCATE TABLE country_languages;",
    "TRUNCATE TABLE country_timezones;",
    "TRUNCATE TABLE currency_countries;",

    "TRUNCATE TABLE languages;",
    "TRUNCATE TABLE timezones;",
    "TRUNCATE TABLE currencies;",

    "TRUNCATE TABLE files;",
    "TRUNCATE TABLE countries;",

    "SET FOREIGN_KEY_CHECKS = 1;",

    "",

    "-- =========================================",
    "-- Insert Data",
    "-- =========================================",

    ""
]

# ============================================================
# Cache
# ============================================================

currency_cache = {}

timezone_cache = {}

language_cache = {}

country_currency_mapping = []

country_timezone_mapping = []

country_language_mapping = []

print("Initialize Success")

# ============================================================
# Country
# ============================================================

print("Generate Countries...")

for country in countries:

    iso2 = country.get("iso2")

    if not iso2:
        continue

    native_name = (
        country.get("native")
        or country.get("native_name")
        or country.get("name")
    )

    sqls.append(f"""
INSERT INTO countries
(
    iso2,
    iso3,
    iso_numeric,
    phone_code,
    name,
    native_name
)
VALUES
(
    {sql(iso2)},
    {sql(country.get("iso3"))},
    {sql(country.get("numeric_code"))},
    {sql(country.get("phone_code"))},
    {sql(country.get("name"))},
    {sql(native_name)}
);
""")

print("Countries Finished")


# ============================================================
# Country Flag
# ============================================================

print("Generate Country Flags...")

for country in countries:

    iso2 = country.get("iso2")

    if not iso2:
        continue

    flag_url = f"https://flagcdn.com/w320/{iso2.lower()}.png"

    sqls.append(f"""
INSERT INTO files
(
    user_id,
    resource_provider_id,
    storage_provider_id,
    entity_type_id,
    entity_id,
    file_role_id,
    original_name,
    storage_key,
    file_path,
    thumbnail_path,
    file_type_id,
    file_size,
    width,
    height,
    checksum,
    is_public,
    sort_order,
    is_system,
    is_active
)
SELECT
    NULL,
    (
        SELECT id
        FROM resource_providers
        WHERE code='{RESOURCE_PROVIDER}'
    ),
    (
        SELECT id
        FROM storage_providers
        WHERE code='{STORAGE_PROVIDER}'
    ),
    (
        SELECT id
        FROM entity_types
        WHERE code='{ENTITY_TYPE}'
    ),
    c.id,
    (
        SELECT id
        FROM file_roles
        WHERE code='{FILE_ROLE}'
    ),
    '{iso2.lower()}.png',
    NULL,
    '{flag_url}',
    NULL,
    (
        SELECT id
        FROM file_types
        WHERE code='{FILE_TYPE}'
    ),
    NULL,
    NULL,
    NULL,
    NULL,
    TRUE,
    0,
    TRUE,
    TRUE
FROM countries c
WHERE c.iso2='{iso2}';
""")

print("Country Flags Finished")

# ============================================================
# Currency
# ============================================================

print("Generate Currencies...")

for country in countries:

    currency_code = country.get("currency")

    if not currency_code:
        continue

    currency_name = country.get("currency_name") or currency_code
    currency_symbol = country.get("currency_symbol")

    if currency_code not in currency_cache:

        decimal_places, is_fiat, is_crypto = get_currency_setting(currency_code)

        currency_cache[currency_code] = {

            "name": currency_name,

            "symbol": currency_symbol,

            "decimal_places": decimal_places,

            "is_fiat": is_fiat,

            "is_crypto": is_crypto

        }

    country_currency_mapping.append(
        (
            country["iso2"],
            currency_code
        )
    )

print(f"Currency Count : {len(currency_cache)}")

# ============================================================
# Insert Currency
# ============================================================

sqls.append("")
sqls.append("-- =========================================")
sqls.append("-- Currency")
sqls.append("-- =========================================")
sqls.append("")

for currency_code in sorted(currency_cache.keys()):

    currency = currency_cache[currency_code]

    sqls.append(f"""
INSERT INTO currencies
(
    code,
    name,
    symbol,
    decimal_places,
    is_fiat,
    is_crypto
)
VALUES
(
    {sql(currency_code)},
    {sql(currency["name"])},
    {sql(currency["symbol"])},
    {sql(currency["decimal_places"])},
    {sql(currency["is_fiat"])},
    {sql(currency["is_crypto"])}
);
""")

print("Currencies Finished")

# ============================================================
# Timezone
# ============================================================

print("Generate Timezones...")

for country in countries:

    iso2 = country.get("iso2")

    if not iso2:
        continue

    timezone_list = country.get("timezones", [])

    if not isinstance(timezone_list, list):
        continue

    for index, timezone in enumerate(timezone_list):

        zone_name = timezone.get("zoneName")

        if not zone_name:
            continue

        abbreviation = timezone.get("abbreviation")

        timezone_name = timezone.get("tzName")

        utc_offset = timezone.get("gmtOffset")

        if utc_offset is None:
            utc_offset = 0

        has_dst = False

        if timezone_name:

            has_dst = "daylight" in timezone_name.lower()

        if zone_name not in timezone_cache:

            timezone_cache[zone_name] = {

                "code": abbreviation,

                "name": timezone_name,

                "offset": utc_offset,

                "has_dst": has_dst

            }

        country_timezone_mapping.append(

            (

                iso2,

                zone_name,

                index == 0

            )

        )

print(f"Timezone Count : {len(timezone_cache)}")

# ============================================================
# Insert Timezone
# ============================================================

sqls.append("")
sqls.append("-- =========================================")
sqls.append("-- Timezones")
sqls.append("-- =========================================")
sqls.append("")

for zone_name in sorted(timezone_cache.keys()):

    timezone = timezone_cache[zone_name]

    sqls.append(f"""
INSERT INTO timezones
(
    code,
    iana_name,
    name,
    utc_offset,
    has_dst
)
VALUES
(
    {sql(timezone["code"])},
    {sql(zone_name)},
    {sql(timezone["name"])},
    {timezone["offset"]},
    {sql(timezone["has_dst"])}
);
""")

print("Timezones Finished")

# ============================================================
# Language
# ============================================================

print("Generate Languages...")

dr5hn_iso2 = {
    country.get("iso2", "").upper()
    for country in countries
    if country.get("iso2")
}

for country in rest_countries:

    codes = country.get("codes") or {}
    iso2 = first_value(codes, "alpha_2", "alpha2")

    if not iso2:
        continue

    iso2 = str(iso2).upper()

    # Only generate mappings for countries that exist in dr5hn countries.json.
    if iso2 not in dr5hn_iso2:
        continue

    raw_languages = country.get("languages") or []

    if isinstance(raw_languages, dict):
        raw_languages = [
            {
                "code": code,
                "name": name
            }
            for code, name in raw_languages.items()
        ]

    if not isinstance(raw_languages, list):
        continue

    language_list = []

    for language in raw_languages:

        if isinstance(language, str):
            language = {
                "code": language,
                "name": language.upper()
            }

        if not isinstance(language, dict):
            continue

        code = language_code(language)

        if not code:
            continue

        name = language_name(language) or code.upper()

        is_official = language_boolean(
            language,
            False,
            "is_official",
            "official"
        )

        is_default = language_boolean(
            language,
            False,
            "is_default",
            "default",
            "primary"
        )

        language_list.append(
            (
                code,
                name,
                is_official,
                is_default
            )
        )

    has_explicit_default = any(
        item[3]
        for item in language_list
    )

    for index, (code, name, is_official, is_default) in enumerate(language_list):

        if code not in language_cache:

            language_cache[code] = {

                "name": name

            }

        country_language_mapping.append(

            (

                iso2,

                code,

                is_official,

                is_default or (
                    not has_explicit_default
                    and index == 0
                )

            )

        )

print(f"Language Count : {len(language_cache)}")

# ============================================================
# Insert Languages
# ============================================================

sqls.append("")
sqls.append("-- =========================================")
sqls.append("-- Languages")
sqls.append("-- =========================================")
sqls.append("")

for code in sorted(language_cache.keys()):

    language = language_cache[code]

    sqls.append(f"""
INSERT INTO languages
(
    code,
    name,
    is_active
)
VALUES
(
    {sql(code)},
    {sql(language["name"])},
    TRUE
);
""")

print("Languages Finished")

# ============================================================
# Country Currency Mapping
# ============================================================

print("Generate Country Currency Mapping...")

sqls.append("")
sqls.append("-- =========================================")
sqls.append("-- Country Currency")
sqls.append("-- =========================================")
sqls.append("")

for iso2, currency_code in country_currency_mapping:

    sqls.append(f"""
INSERT INTO currency_countries
(
    country_id,
    currency_id
)
SELECT
    c.id,
    cu.id
FROM countries c
JOIN currencies cu
    ON cu.code = {sql(currency_code)}
WHERE c.iso2 = {sql(iso2)};
""")

print("Country Currency Finished")


# ============================================================
# Country Timezone Mapping
# ============================================================

print("Generate Country Timezone Mapping...")

sqls.append("")
sqls.append("-- =========================================")
sqls.append("-- Country Timezone")
sqls.append("-- =========================================")
sqls.append("")

for iso2, zone_name, is_default in country_timezone_mapping:

    sqls.append(f"""
INSERT INTO country_timezones
(
    country_id,
    timezone_id,
    is_default
)
SELECT
    c.id,
    t.id,
    {sql(is_default)}
FROM countries c
JOIN timezones t
    ON t.iana_name = {sql(zone_name)}
WHERE c.iso2 = {sql(iso2)};
""")

print("Country Timezone Finished")


# ============================================================
# Country Language Mapping
# ============================================================

print("Generate Country Language Mapping...")

sqls.append("")
sqls.append("-- =========================================")
sqls.append("-- Country Language")
sqls.append("-- =========================================")
sqls.append("")

for iso2, language_code, is_official, is_default in country_language_mapping:

    sqls.append(f"""
INSERT INTO country_languages
(
    country_id,
    language_id,
    is_official,
    is_default
)
SELECT
    c.id,
    l.id,
    {sql(is_official)},
    {sql(is_default)}
FROM countries c
JOIN languages l
    ON l.code = {sql(language_code)}
WHERE c.iso2 = {sql(iso2)};
""")

print("Country Language Finished")

# ============================================================
# Summary
# ============================================================

sqls.append("")
sqls.append("-- =========================================")
sqls.append("-- Finished")
sqls.append("-- =========================================")
sqls.append("")

print("")
print("===========================================")
print("Summary")
print("===========================================")

print(f"Countries               : {len(countries)}")
print(f"Currencies              : {len(currency_cache)}")
print(f"Timezones               : {len(timezone_cache)}")
print(f"Languages               : {len(language_cache)}")

print(f"Country Currency Map    : {len(country_currency_mapping)}")
print(f"Country Timezone Map    : {len(country_timezone_mapping)}")
print(f"Country Language Map    : {len(country_language_mapping)}")

print(f"SQL Count               : {len(sqls)}")

# ============================================================
# Output SQL
# ============================================================

print("")
print("Writing SQL File...")

try:

    with open(
        OUTPUT_FILE,
        "w",
        encoding="utf-8"
    ) as f:

        f.write("\n".join(sqls))

    print("")
    print("===========================================")
    print("Generate Success")
    print("===========================================")

    print(f"Output : {OUTPUT_FILE}")

except Exception as ex:

    print("")
    print("===========================================")
    print("Generate Failed")
    print("===========================================")

    print(ex)

print("")
print("Done.")
