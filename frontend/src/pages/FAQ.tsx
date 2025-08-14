import React, { useState } from 'react';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import { Button, Chip, Container, Typography, Box, Collapse, Paper, Stack } from '@mui/material';

interface FAQItem {
  id: string;
  question: string;
  answer: string;
  category: string;
}

const faqData: FAQItem[] = [
  // General
  {
    id: 'general-1',
    category: 'General',
    question: 'What is this voting system?',
    answer: 'This is a blockchain-powered platform that allows community members to participate in secure and transparent voting. Votes are recorded on the blockchain to ensure integrity and auditability.'
  },
  {
    id: 'general-2',
    category: 'General',
    question: 'Why is blockchain used for voting?',
    answer: 'Blockchain provides tamper-proof records, transparency, and verifiable results while protecting voter privacy.'
  },
  {
    id: 'general-3',
    category: 'General',
    question: 'Who can participate in voting?',
    answer: 'Any eligible member with a supported cryptocurrency wallet can participate or delegate their vote.'
  },
  {
    id: 'general-4',
    category: 'General',
    question: 'Why does it take longer to create or edit ballots?',
    answer: 'Storing all ballot data on the blockchain requires additional processing time.'
  },
  // Wallet & Connection
  {
    id: 'wallet-1',
    category: 'Wallet & Connection',
    question: 'Why do I need to connect my wallet?',
    answer: 'Connecting your wallet verifies your identity on the blockchain and proves you are eligible to vote or delegate your voting rights.'
  },
  {
    id: 'wallet-2',
    category: 'Wallet & Connection',
    question: 'Which wallets are supported?',
    answer: 'We currently support MetaMask, WalletConnect, and Coinbase Wallet. More wallet integrations are planned.'
  },
  {
    id: 'wallet-3',
    category: 'Wallet & Connection',
    question: 'Is it safe to connect my wallet?',
    answer: 'Yes. We only request permission to view your public address and sign messages. We will never request your private keys or seed phrase.'
  },
  {
    id: 'wallet-4',
    category: 'Wallet & Connection',
    question: "What if I don't have a wallet?",
    answer: 'You can create one using any supported wallet provider and then connect it to the platform.'
  },
  // Voting & Delegation
  {
    id: 'voting-1',
    category: 'Voting & Delegation',
    question: 'What is proxy voting (delegated voting)?',
    answer: 'Proxy voting allows you to assign your voting power to another trusted participant who can vote on your behalf.'
  },
  {
    id: 'voting-2',
    category: 'Voting & Delegation',
    question: 'How do I delegate my vote?',
    answer: 'After connecting your wallet, navigate to the "Delegate Vote" section, select a delegate, and sign the delegation transaction.'
  },
  {
    id: 'voting-3',
    category: 'Voting & Delegation',
    question: 'Can I change or revoke my delegation?',
    answer: 'Yes. You can reassign your delegate or revoke your delegation at any time before voting closes.'
  },
  {
    id: 'voting-4',
    category: 'Voting & Delegation',
    question: 'Does delegation mean I lose control of my wallet?',
    answer: 'No. Delegation only applies to voting power. You still have full control over your funds and assets.'
  },
  // Security & Privacy
  {
    id: 'security-1',
    category: 'Security & Privacy',
    question: 'Can anyone see who I voted for?',
    answer: 'No. While the blockchain records that you voted (or delegated), your actual vote choice remains private.'
  },
  {
    id: 'security-2',
    category: 'Security & Privacy',
    question: 'How do you prevent vote tampering?',
    answer: 'All votes are cryptographically signed and recorded on the blockchain. Results are verifiable by anyone.'
  },
  {
    id: 'security-3',
    category: 'Security & Privacy',
    question: 'Will this cost any gas fees?',
    answer: 'Depending on the blockchain network used, there may be minimal transaction fees for voting or delegation. We display estimated costs before confirmation.'
  },
  {
    id: 'security-4',
    category: 'Security & Privacy',
    question: 'What happens if I lose access to my wallet?',
    answer: 'If you lose your wallet or keys, you will lose your voting eligibility for that session, as we cannot reset blockchain-based identities.'
  },
  // Technical & Governance
  {
    id: 'technical-1',
    category: 'Technical & Governance',
    question: 'Which blockchain network is used?',
    answer: 'We currently run on Ethereum Sepolia testnet. All voting transactions are visible on the public blockchain.'
  },
  {
    id: 'technical-2',
    category: 'Technical & Governance',
    question: 'How do I verify the vote results?',
    answer: 'After the vote ends, you can view the blockchain transaction records or use our integrated verification tool.'
  },
  {
    id: 'technical-3',
    category: 'Technical & Governance',
    question: 'Who controls the platform?',
    answer: 'The system is governed by our organization, and its code is open-source for public auditing.'
  },
  {
    id: 'technical-4',
    category: 'Technical & Governance',
    question: 'Can smart contracts be audited?',
    answer: 'Yes. All smart contracts have been audited, and the reports are publicly available.'
  },
  {
    id: 'technical-5',
    category: 'Technical & Governance',
    question: 'What if I suspect a security issue?',
    answer: 'Please report any vulnerabilities via our responsible disclosure program through our contact support.'
  }
];

const FAQAccordion: React.FC<{ item: FAQItem }> = ({ item }) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <Paper elevation={1} className="mb-2">
      <Box
        className="flex justify-between items-center px-6 py-4 cursor-pointer"
        onClick={() => setIsOpen(!isOpen)}
      >
        <Typography variant="subtitle1" className="font-medium text-gray-900">
          {item.question}
        </Typography>
        {isOpen ? (
          <ExpandLessIcon className="text-gray-500" />
        ) : (
          <ExpandMoreIcon className="text-gray-500" />
        )}
      </Box>
      <Collapse in={isOpen}>
        <Box className="px-6 pb-4">
          <Typography variant="body2" className="text-gray-600 leading-relaxed">
            {item.answer}
          </Typography>
        </Box>
      </Collapse>
    </Paper>
  );
};

const FAQ: React.FC = () => {
  const [selectedCategory, setSelectedCategory] = useState<string>('All');
  const categories = ['All', ...Array.from(new Set(faqData.map(item => item.category)))];
  const filteredFAQs = selectedCategory === 'All'
    ? faqData
    : faqData.filter(item => item.category === selectedCategory);

  return (
    <div className="min-h-screen bg-gray-100 p-4 sm:p-6 lg:p-8">
      <Container maxWidth="md">
        <Box className="text-center mb-8">
          <Typography variant="h3" className="font-bold text-gray-900 mb-4">
            FAQs
          </Typography>
          <Typography variant="body1" className="text-lg text-gray-600 max-w-2xl mx-auto">
            Find answers to common questions about our blockchain voting system.
            If you can't find what you're looking for, please contact our support team.
          </Typography>
        </Box>

        {/* Category Filter */}
        <Stack direction="row" spacing={1} justifyContent="center" className="mb-8 flex-wrap">
          {categories.map((category) => (
            <Chip
              key={category}
              label={category}
              color={selectedCategory === category ? "primary" : "default"}
              onClick={() => setSelectedCategory(category)}
              className="cursor-pointer"
            />
          ))}
        </Stack>

        {/* FAQ Items */}
        <Box className="bg-white rounded-lg shadow-sm">
          {filteredFAQs.length > 0 ? (
            filteredFAQs.map((item) => (
              <FAQAccordion key={item.id} item={item} />
            ))
          ) : (
            <Box className="p-8 text-center text-gray-500">
              No FAQs found for the selected category.
            </Box>
          )}
        </Box>

        {/* Contact Section */}
        <Box className="mt-12 bg-blue-50 rounded-lg p-6 text-center">
          <Typography variant="h6" className="text-xl font-semibold text-gray-900 mb-2">
            Still have questions?
          </Typography>
          <Typography className="text-gray-600 mb-4">
            Our support team is here to help you with any additional questions.
          </Typography>
          <Button variant="contained" color="primary" size="large"
            onClick={() => window.location.href = "mailto:2981436@student.gla.ac.uk?subject=Blockchain Voting Support&body=Hi, I want to buy you a cup of coffee ( ´▽｀)"}>
            Contact Support
          </Button>
        </Box>
      </Container>
    </div>
  );
};

export default FAQ;