"""Resource regression: run with python3 -m unittest discover -s tests -v."""
from pathlib import Path
import unittest
import xml.etree.ElementTree as ET


class KanLabelsTest(unittest.TestCase):
    def test_english_kan_labels_match_action_labels(self):
        resources = Path(__file__).resolve().parents[1] / 'shared/src/commonMain/composeResources'
        strings = {s.attrib['name']: s.text for s in ET.parse(resources / 'values/strings.xml').getroot()}
        self.assertEqual('Concealed-kong', strings['label_ankan'])
        self.assertEqual('Melded-kong', strings['label_minkan'])
        self.assertEqual(strings['label_ankan'], strings['text_shanten_action_ankan'].split(' %')[0])
        self.assertEqual(strings['label_minkan'], strings['text_shanten_action_minkan'])

    def test_validation_message_available_in_all_supported_languages(self):
        resources = Path(__file__).resolve().parents[1] / 'shared/src/commonMain/composeResources'
        for locale in ('values', 'values-ja', 'values-zh'):
            with self.subTest(locale=locale):
                strings = {s.attrib['name']: s.text for s in ET.parse(resources / locale / 'strings.xml').getroot()}
                self.assertTrue(strings['text_chankan_tile_in_hand'])
